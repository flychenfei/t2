package com.britesnow.samplesocial.service;

import java.util.Map;

import org.scribe.model.OAuthRequest;
import org.scribe.model.Verb;

import com.britesnow.snow.util.JsonUtil;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class DropboxFileService {
	@Inject
	private DropboxAuthService dropboxAuthService;
	
	public Map getMetadata(String path,Long userId){
		OAuthRequest request = new OAuthRequest(Verb.GET,"https://api.dropbox.com/1/metadata/dropbox");
		dropboxAuthService.setAuthorizationHeader(request, userId);
    	String metadata = request.send().getBody();
    	return JsonUtil.toMapAndList(metadata);
	}
	
}
