package com.britesnow.samplesocial.service;

import org.scribe.model.OAuthRequest;
import org.scribe.model.Verb;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class DropboxFileService {
	@Inject
	private DropboxAuthService dropboxAuthService;
	
	public void getFiles(String path,Long userId){
		OAuthRequest request = new OAuthRequest(Verb.GET,"https://api.dropbox.com/1/metadata/dropbox");
		dropboxAuthService.setAuthorizationHeader(request, userId);
    	System.out.println(request.send().getBody());
	}
	
}
