package com.britesnow.samplesocial.service;

import java.io.InputStream;
import java.util.Locale;
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
	private static String FILES = "https://api-content.dropbox.com/1/files/dropbox";
	private static String CREATEFOLDER = "https://api.dropbox.com/1/fileops/create_folder";
	private static String DELETE = "https://api.dropbox.com/1/fileops/delete";
	public Map getMetadata(String path,Long userId,Locale locale){
		OAuthRequest request = new OAuthRequest(Verb.GET,METADATA+path);
		dropboxAuthService.setAuthorizationHeader(request, userId);
		request.addQuerystringParameter("locale", locale.toString());
    	String metadata = request.send().getBody();
    	return JsonUtil.toMapAndList(metadata);
	}
	
	public InputStream getThumbnails(String path,Long userId){
		OAuthRequest request = new OAuthRequest(Verb.GET,THUMBNAILS+path);
		dropboxAuthService.setAuthorizationHeader(request, userId);
    	return request.send().getStream();
	}
	
	public InputStream getFile(String path,Long userId){
		OAuthRequest request = new OAuthRequest(Verb.GET,FILES+path);
		dropboxAuthService.setAuthorizationHeader(request, userId);
		return request.send().getStream();
	}
	
	public Map createFolder(String path,Long userId){
		OAuthRequest request = new OAuthRequest(Verb.POST,CREATEFOLDER);
		dropboxAuthService.setAuthorizationHeader(request, userId);
		request.addBodyParameter("path", path);
		request.addBodyParameter("root", "dropbox");
    	String metadata = request.send().getBody();
    	System.out.println(metadata);
    	return JsonUtil.toMapAndList(metadata);
	}
	
	public Map delete(String path,Long userId){
		OAuthRequest request = new OAuthRequest(Verb.POST,DELETE);
		dropboxAuthService.setAuthorizationHeader(request, userId);
		request.addBodyParameter("path", path);
		request.addBodyParameter("root", "dropbox");
    	String metadata = request.send().getBody();
    	System.out.println(metadata);
    	return JsonUtil.toMapAndList(metadata);
	}
}
