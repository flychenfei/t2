package com.britesnow.samplesocial.service;

import java.util.Map;

import org.scribe.model.OAuthRequest;
import org.scribe.model.Verb;

import com.britesnow.snow.util.JsonUtil;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class DropboxAccountService {
	
	private static String ACCOUNTINFO = "https://api.dropbox.com/1/account/info";
	@Inject
	private DropboxAuthService dropboxAuthService;
	
	public Map getAccountInfo(Long userId){
		OAuthRequest request = new OAuthRequest(Verb.GET,ACCOUNTINFO);
		dropboxAuthService.setAuthorizationHeader(request, userId);
    	String accountInfo = request.send().getBody();
    	return JsonUtil.toMapAndList(accountInfo);
	}
	
}
