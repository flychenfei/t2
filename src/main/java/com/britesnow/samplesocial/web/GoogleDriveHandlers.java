package com.britesnow.samplesocial.web;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;

import com.britesnow.samplesocial.service.GoogleDriveService;
import com.britesnow.samplesocial.util.GoogleDriveDataPack;
import com.britesnow.snow.util.Pair;
import com.britesnow.snow.web.RequestContext;
import com.britesnow.snow.web.handler.annotation.WebResourceHandler;
import com.britesnow.snow.web.param.annotation.WebParam;
import com.britesnow.snow.web.rest.annotation.WebGet;
import com.britesnow.snow.web.rest.annotation.WebPost;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class GoogleDriveHandlers {
    @Inject
    private GoogleDriveService googleDriveService;


    @WebGet("/googleDrive/filelist")
    public Object listFiles(@WebParam("parentld") String parentld,@WebParam("pageIndex") String nextPagetoken,@WebParam("pageSize") Integer pageSize){
    	GoogleDriveDataPack results = googleDriveService.list(parentld, nextPagetoken, pageSize, false);
		List<Map> docsInfo = results.getDetailData();
		WebResponse result = WebResponse.success(docsInfo);
		result.set("nextPageToken", results.getGeneralData().get("next"));
		result.set("parentId", results.getGeneralData().get("parentId"));
    	return result;
    }
    
    @WebGet("/googleDrive/search")
    public Object searchFile(@WebParam("keyword") String keyword, @WebParam("searchType") String searchType, @WebParam("startDate") String startDate, @WebParam("endDate") String endDate, @WebParam("pageIndex") String nextPagetoken,@WebParam("pageSize") Integer pageSize){
    	Pair<String, List<Map>> pair = googleDriveService.searchFile(keyword, searchType, startDate, endDate, nextPagetoken, pageSize);
		List<Map> docsInfo = pair.getSecond();
		WebResponse result = WebResponse.success(docsInfo);
		result.set("nextPageToken", pair.getFirst());
    	return result;
    }
    
    @WebPost("/googleDrive/upload")
	public WebResponse upload(@WebParam("file") FileItem file){
    	if(file==null){
    		return WebResponse.fail();
    	}
		if(googleDriveService.uploadFile(file)){
			return WebResponse.success();
		}
		return WebResponse.fail();
	}
    
    @WebGet("/googleDrive/trashFile")
    public Object trashFile(@WebParam("fileId") String fileId){
        if(googleDriveService.trashFile(fileId))
        	return WebResponse.success();
        else
        	return WebResponse.fail();
    }
    
    @WebGet("/googleDrive/untrashFile")
    public Object untrashFile(@WebParam("fileId") String fileId){
        if(googleDriveService.untrashFile(fileId))
        	return WebResponse.success();
        else
        	return WebResponse.fail();
    }
    
    @WebGet("/googleDrive/copyFile")
    public Object copyFile(@WebParam("fileId") String fileId, @WebParam("copyTitle") String copyTitle){
    	if(googleDriveService.copyFile(fileId, copyTitle))
        	return WebResponse.success();
        else
        	return WebResponse.fail();
    }
    
    @WebGet("/googleDrive/deleteFile")
    public Object deleteFile(@WebParam("fileId") String fileId){
        if(googleDriveService.deleteFile(fileId))
        	return WebResponse.success();
        else
        	return WebResponse.fail();
    }
    
    @WebResourceHandler(matches="/googleDrive/download")
    public void download(@WebParam("fileId") String fileId, @WebParam("fileName") String fileName,  RequestContext rc) throws IOException {
    	InputStream in = googleDriveService.download(fileId);
    	if(in != null){
    		HttpServletResponse res = rc.getRes();
    		res.addHeader("Content-Disposition", "attachment;filename="+fileName);
    		OutputStream out = res.getOutputStream();
    		res.setContentType("application/octet-stream");
    		int length = 0;
    		byte[] data = new byte[10240];
    		while((length=in.read(data))!=-1){
    			out.write(data, 0, length);
    		}
    		in.close();
    		out.close();
    	}
    }
    
    @WebGet("/googleDrive/trashlist")
    public Object listTrash(@WebParam("pageIndex") String nextPagetoken,@WebParam("pageSize") Integer pageSize){
    	GoogleDriveDataPack results = googleDriveService.list(null, nextPagetoken, pageSize,true);
    	List<Map> docsInfo = results.getDetailData();
		WebResponse result = WebResponse.success(docsInfo);
		result.set("nextPageToken", results.getGeneralData().get("next"));
    	return result;
    }
    
    @WebGet("/googleDrive/childList")
    public Object listChild(@WebParam("selfId") String selfId,@WebParam("pageIndex") String nextPagetoken,@WebParam("pageSize") Integer pageSize){
    	GoogleDriveDataPack results = googleDriveService.list(selfId, nextPagetoken, pageSize, false);
    	List<Map> docsInfo = results.getDetailData();
		WebResponse result = WebResponse.success(docsInfo);
		result.set("nextPageToken", results.getGeneralData().get("next"));
		result.set("parentId", results.getGeneralData().get("parentId"));
    	return result;
    }
    
    @WebGet("/googleDrive/restoreTrash")
    public Object restoreTrash(){
    	if(googleDriveService.restoreTrash())
        	return WebResponse.success();
        else
        	return WebResponse.fail();
    }
    
    @WebGet("/googleDrive/emptyTrash")
    public Object emptyTrash(){
    	if(googleDriveService.emptyTrash())
        	return WebResponse.success();
        else
        	return WebResponse.fail();
    }
    
    @WebGet("/googleDrive/createFolder")
    public Object createFolder(@WebParam("folderName") String folderName, @WebParam("parentId") String parentId){
    	System.out.println(parentId+"========"+folderName);
    	if(googleDriveService.createFolder(parentId, folderName))
        	return WebResponse.success();
        else
        	return WebResponse.fail();
    }
}
