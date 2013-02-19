package com.britesnow.samplesocial.web;

import com.britesnow.samplesocial.entity.User;
import com.britesnow.samplesocial.service.DropboxFileService;
import com.britesnow.snow.web.param.annotation.WebParam;
import com.britesnow.snow.web.param.annotation.WebUser;
import com.britesnow.snow.web.rest.annotation.WebGet;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class DropboxFileHandler {
	
	@Inject
	private DropboxFileService dropboxFileService;
	
	@WebGet("/dropbox/getMetadata")
	public WebResponse getFileMetadata(@WebParam("path") String path,@WebUser User user){
		if(path==null)
			path="";
		dropboxFileService.getThumbnails(path, user.getId());
		return WebResponse.success(dropboxFileService.getMetadata(path, user.getId()));
	}
	
}
