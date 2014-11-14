package com.britesnow.samplesocial.service;

import java.util.HashMap;
import java.util.Map;

import org.scribe.model.OAuthRequest;
import org.scribe.model.Token;
import org.scribe.model.Verb;

import com.britesnow.samplesocial.entity.User;
import com.britesnow.samplesocial.manager.OAuthManager;
import com.britesnow.samplesocial.model.SocialIdEntity;
import com.britesnow.samplesocial.oauth.OauthException;
import com.britesnow.samplesocial.oauth.ServiceType;
import com.britesnow.snow.web.binding.ApplicationProperties;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;
import com.dropbox.client2.session.WebAuthSession;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class DropboxAuthService implements AuthService{

	@Inject
    private OAuthManager oAuthManager;
	
	@Inject
	@ApplicationProperties
	private Map configMap;
	
	private static String DROPBOX = "dropbox";
	private static String REQUEST_TOKEN = "https://api.dropbox.com/1/oauth/request_token";
	private static String AUTHORIZE = "https://www.dropbox.com/1/oauth/authorize?";
	private static String ACCESS_TOKEN = "https://api.dropbox.com/1/oauth/access_token";
	private  Map<String,Token> authTokenMap = new HashMap<String,Token>();
    public DropboxAuthService() {
    }
    
    public SocialIdEntity getSocialIdEntity() {
		SocialIdEntity socialId = oAuthManager.getSocialIdEntityfromSession(ServiceType.Dropbox);
        if(socialId == null){
        	//if result is null, need redo auth
        	throw new OauthException(getAuthorizationUrl());
        }
        return socialId;
    }
	
    public String getAuthorizationUrl() {
    	OAuthRequest request = new OAuthRequest(Verb.POST,REQUEST_TOKEN);
    	StringBuffer requestHeader = new StringBuffer("OAuth ");
    	requestHeader.append("oauth_version=\"1.0\",")
    				 .append("oauth_signature_method=\"PLAINTEXT\",")
    	             .append("oauth_consumer_key=\"")
    	             .append(configMap.get(DROPBOX+".app_key"))
	             	 .append("\", oauth_signature=\"")
	             	 .append(configMap.get(DROPBOX+".app_secret"))
	             	 .append("&\"");
    	
    	request.addHeader("Authorization", 
    			requestHeader.toString());
    	String authTokenString = request.send().getBody();
    	Token authToken = tokenHandler(authTokenString);
    	authTokenMap.put(authToken.getToken(), authToken);
        return AUTHORIZE+"oauth_token="+authToken.getToken()+
        		"&oauth_callback="+configMap.get(DROPBOX+".oauth_callback");
    }
    
    public AppKeyPair getAppKeyPair(){
    	return new AppKeyPair(configMap.get(DROPBOX+".app_key").toString()
    			,configMap.get(DROPBOX+".app_secret").toString());
    }
    
    public Token getAccessToken(Token authToken){
    	OAuthRequest request = new OAuthRequest(Verb.POST,ACCESS_TOKEN);
    	StringBuffer requestHeader = new StringBuffer("OAuth ");
    	requestHeader.append("oauth_version=\"1.0\",")
    				 .append("oauth_signature_method=\"PLAINTEXT\",")
    	             .append("oauth_consumer_key=\"")
    	             .append(configMap.get(DROPBOX+".app_key"))
	             	 .append("\",oauth_token=\""+authToken.getToken()+"\",")
	             	 .append(" oauth_signature=\"")
	             	 .append(configMap.get(DROPBOX+".app_secret"))
	             	 .append("&").append(authToken.getSecret()).append("\"");
    	
    	request.addHeader("Authorization", 
    			requestHeader.toString());
    	String access_token = request.send().getBody();
        return tokenHandler(access_token);
    }
    
    public void setAuthorizationHeader(OAuthRequest request){
    	SocialIdEntity soId =getSocialIdEntity();
    	StringBuffer requestHeader = new StringBuffer("OAuth ");
    	requestHeader.append("oauth_version=\"1.0\",")
    				 .append("oauth_signature_method=\"PLAINTEXT\",")
    	             .append("oauth_consumer_key=\"")
    	             .append(configMap.get(DROPBOX+".app_key"))
	             	 .append("\",oauth_token=\""+soId.getToken()+"\",")
	             	 .append(" oauth_signature=\"")
	             	 .append(configMap.get(DROPBOX+".app_secret"))
	             	 .append("&").append(soId.getSecret()).append("\"");
    	request.addHeader("Authorization", 
    			requestHeader.toString());
    }
    
    public WebAuthSession getWebAuthSession(){
    	return new WebAuthSession(new AppKeyPair(
    			configMap.get(DROPBOX+".app_key").toString(),configMap.get(DROPBOX+".app_secret").toString()), AccessType.DROPBOX);
    }
    public String getHeader(){
    	SocialIdEntity soId =getSocialIdEntity();
    	StringBuffer requestHeader = new StringBuffer("OAuth ");
    	requestHeader.append("oauth_version=\"1.0\",")
    				 .append("oauth_signature_method=\"PLAINTEXT\",")
    	             .append("oauth_consumer_key=\"")
    	             .append(configMap.get(DROPBOX+".app_key"))
	             	 .append("\",oauth_token=\""+soId.getToken()+"\",")
	             	 .append(" oauth_signature=\"")
	             	 .append(configMap.get(DROPBOX+".app_secret"))
	             	 .append("&").append(soId.getSecret()).append("\"");
    	return requestHeader.toString();
    }
    
	public void updateAccessToken(Token authToken, User user) {
		try {
			Token accessToken = getAccessToken(authToken);
			
			HashMap<String, String> map = new HashMap<String, String>();
            map.put("email", null);
            map.put("access_token", accessToken.getToken());
            map.put("secret", accessToken.getSecret());
            oAuthManager.setInfo(ServiceType.Dropbox, map);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new OauthException(getAuthorizationUrl());
		}
	}
	
    public Token getTokenByAuthToken(String authToken){
    	return authTokenMap.get(authToken);
    }
    
    private Token tokenHandler(String authTokenString){
    	String[] authToken = authTokenString.split("&");
    	String oauth_token="",oauth_token_secret="";
    	for(int i = 0,j=authToken.length;i<j;i++){
    		if(authToken[i].contains("oauth_token="))
    			oauth_token = authToken[i].split("=")[1];
    		else if(authToken[i].contains("oauth_token_secret="))
    			oauth_token_secret = authToken[i].split("=")[1];
    	}
    	return new Token(oauth_token,oauth_token_secret);
    }
    
}