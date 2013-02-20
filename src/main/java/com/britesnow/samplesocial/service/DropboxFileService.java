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
	
	private static String METADATA = "https://api.dropbox.com/1/metadata/dropbox";
	private static String THUMBNAILS = "https://api-content.dropbox.com/1/thumbnails/dropbox";
	public Map getMetadata(String path,Long userId){
		OAuthRequest request = new OAuthRequest(Verb.GET,METADATA+path);
		dropboxAuthService.setAuthorizationHeader(request, userId);
    	String metadata = request.send().getBody();
    	System.out.println(metadata);
    	return JsonUtil.toMapAndList(metadata);
	}
	
	public void getThumbnails(String path,Long userId){
		OAuthRequest request = new OAuthRequest(Verb.GET,THUMBNAILS+"/Photos/Sample Album/Boston City Flow.jpg");
		dropboxAuthService.setAuthorizationHeader(request, userId);
    	//System.out.println(request.send().getBody());
	}
}
