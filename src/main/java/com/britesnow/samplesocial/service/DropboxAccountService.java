package com.britesnow.samplesocial.service;

import java.util.Map;

import org.scribe.model.OAuthRequest;
import org.scribe.model.Verb;

import com.britesnow.samplesocial.entity.SocialIdEntity;
import com.britesnow.snow.util.JsonUtil;
import com.britesnow.snow.web.binding.ApplicationProperties;
import com.google.inject.Inject;


public class DropboxAccountService {
	
	private static String DROPBOX = "dropbox";
	private static String ACCOUNTINFO = "https://api.dropbox.com/1/account/info";
	@Inject
	private DropboxAuthService dropboxAuthService;
	
	@Inject
	@ApplicationProperties
	private Map configMap;
	
	public Map getAccountInfo(Long userId){
		SocialIdEntity soId = dropboxAuthService.getSocialIdEntity(userId);
		OAuthRequest request = new OAuthRequest(Verb.GET,ACCOUNTINFO);
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
    	String accountInfo = request.send().getBody();
    	System.out.println(accountInfo);
    	return JsonUtil.toMapAndList(accountInfo);
	}
	
}
