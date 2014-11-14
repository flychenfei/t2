package com.britesnow.samplesocial.service;

import static org.scribe.model.OAuthConstants.EMPTY_TOKEN;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.UserService;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import com.britesnow.samplesocial.manager.OAuthManager;
import com.britesnow.samplesocial.model.SocialIdEntity;
import com.britesnow.samplesocial.oauth.OAuthServiceHelper;
import com.britesnow.samplesocial.oauth.OauthException;
import com.britesnow.samplesocial.oauth.ServiceType;
import com.britesnow.snow.web.binding.ApplicationProperties;
import com.google.inject.Inject;
import com.google.inject.Singleton;


@Singleton
public class GithubAuthService implements AuthService {

    private OAuthService oAuthService;
    private Map configMap;
    @Inject
    private OAuthManager oAuthManager;
    @Inject
    public GithubAuthService(OAuthServiceHelper oauthServiceHelper, @ApplicationProperties Map configMap) {
        oAuthService = oauthServiceHelper.getOauthService(ServiceType.Github);
        this.configMap = configMap;
    }
    
	public SocialIdEntity getSocialIdEntity() {
		SocialIdEntity socialId = oAuthManager.getSocialIdEntityfromSession(ServiceType.Github);
        if(socialId == null){
        	//if result is null, need redo auth
        	throw new OauthException(getAuthorizationUrl());
        }
        return socialId;
    }
   
    public String getAuthorizationUrl() {
        return oAuthService.getAuthorizationUrl(EMPTY_TOKEN);
    }

    public boolean updateAccessToken(String verifierCode) throws IOException {
    	OAuthRequest request = new OAuthRequest(Verb.POST, "https://github.com/login/oauth/access_token");
    	String prefix = "github";
    	request.addBodyParameter("code", verifierCode);
    	request.addBodyParameter("client_id", configMap.get(prefix+".client_id").toString());
    	request.addBodyParameter("client_secret",  configMap.get(prefix+".secret").toString());
    	String tokenString = request.send().getBody();
    	String token="";
    	String[] tokenArray = tokenString.split("&");
    	for(String s:tokenArray){
    		if(s.startsWith("access_token")){
    			token = s.substring(13);
    		}
    	}
    	Token accessToken  = new Token(token, configMap.get(prefix+".secret").toString());
    	OAuthRequest requestAccessToken = new OAuthRequest(Verb.GET,"https://api.github.com/user");
    	requestAccessToken.addQuerystringParameter("access_token", token);
    	//String accessTokenString = requestAccessToken.send().getBody();

    	//    	Verifier verifier = new Verifier(verifierCode);
        //        Token accessToken = oAuthService.getAccessToken(EMPTY_TOKEN, verifier);
    	
        if (accessToken.getToken() != null) {
            //get userinfo
            GitHubClient client = new GitHubClient();
            client.setOAuth2Token(accessToken.getToken());
            UserService userService = new UserService(client);
            User ghUser = userService.getUser();
            
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("email", ghUser.getEmail());
            map.put("access_token", accessToken.getToken());
            map.put("secret", configMap.get(prefix+".secret").toString());
            oAuthManager.setInfo(ServiceType.Github, map);
            return true;
        }
        throw new OauthException(getAuthorizationUrl());

    }

    public GitHubClient createClient(com.britesnow.samplesocial.entity.User user){
		GitHubClient client = new GitHubClient();
		client.setOAuth2Token(getToken(user).getToken());
		client.setUserAgent("GitHubJava/2.1.0");
		return client;
    }

    public Token getToken(com.britesnow.samplesocial.entity.User user) {
		SocialIdEntity soId = getSocialIdEntity();
		return new Token(soId.getToken(), soId.getSecret());
	}

    public OAuthRequest createRequest(Verb verb, String url) {
		OAuthRequest request = new OAuthRequest(verb, url);
		 request.addHeader("x-li-format","json");
		return request;
	}
}
