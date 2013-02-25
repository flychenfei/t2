package com.britesnow.samplesocial.web;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;

import com.britesnow.samplesocial.entity.User;
import com.britesnow.samplesocial.service.DropboxFileService;
import com.britesnow.snow.util.JsonUtil;
import com.britesnow.snow.web.RequestContext;
import com.britesnow.snow.web.handler.annotation.WebResourceHandler;
import com.britesnow.snow.web.param.annotation.WebParam;
import com.britesnow.snow.web.param.annotation.WebPath;
import com.britesnow.snow.web.param.annotation.WebUser;
import com.britesnow.snow.web.rest.annotation.WebGet;
import com.britesnow.snow.web.rest.annotation.WebPost;
import com.dropbox.client2.exception.DropboxException;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class DropboxFileHandler {
	
	@Inject
	private DropboxFileService dropboxFileService;
	
	@WebGet("/dropbox/getMetadata")
	public WebResponse getFileMetadata(@WebParam("path") String path,@WebParam("include_deleted") Boolean includeDeleted,@WebUser User user,RequestContext rc){
		if(path==null)
			path="";
		if(includeDeleted==null)
			includeDeleted = false;
		return WebResponse.success(dropboxFileService.getMetadata(path, user.getId(),includeDeleted,rc.getReq().getLocale()));
	}
	
	@WebResourceHandler(matches="/dropbox/getFile/.*")
	public void getFile(@WebPath String path,@WebUser User user,RequestContext rc) throws IOException{
		path = path.substring("/dropbox/getFile".length());
		InputStream in = dropboxFileService.getFile(path, user.getId());
		HttpServletResponse res = rc.getRes();
		res.addHeader("Content-Disposition", "attachment;filename="+path.substring(path.lastIndexOf("/")+1));
		res.addHeader("Content-Length", "" + in.available());
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
	
	@WebResourceHandler(matches="/dropbox/thumbnails/.*")
	public void getThumbnails(@WebPath String path,@WebUser User user,RequestContext rc) throws IOException{
		path = path.substring("/dropbox/thumbnails".length());
		System.out.println(path);
		InputStream in = dropboxFileService.getThumbnails(path, user.getId());
		HttpServletResponse res = rc.getRes();
		OutputStream out = res.getOutputStream();
		res.setContentType("image/jpeg");
		int length = 0;
		byte[] data = new byte[10240];
		while((length=in.read(data))!=-1){
			out.write(data, 0, length);
		}
		in.close();
		out.close();
	}
	
	@WebPost("/dropbox/createFolder")
	public WebResponse createFolder(@WebParam("path") String path,@WebUser User user){
		return WebResponse.success(dropboxFileService.createFolder(path, user.getId()));
	}
	
	@WebPost("/dropbox/delete")
	public WebResponse delete(@WebParam("path") String path,@WebUser User user){
		return WebResponse.success(dropboxFileService.delete(path, user.getId()));
	}
	
	@WebPost("/dropbox/upload")
	public WebResponse upload(@WebParam("data") String data, @WebParam("file") FileItem file,@WebUser User user) throws IOException, DropboxException{
		String path = (String) JsonUtil.toMapAndList(data).get("path");
		if(!path.endsWith("/"))
			path=path+"/";
		path +=file.getName();
		return WebResponse.success(dropboxFileService.upload(file, path, user.getId()));
	}
	
	@WebGet("/dropbox/share")
	public WebResponse share(@WebParam("path") String path,@WebUser User user){
		return WebResponse.success(dropboxFileService.share(path,user.getId()));
	}
}
