package com.britesnow.samplesocial.web;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

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
		if(file.getSize()<150*1024*1024)
			return WebResponse.success(dropboxFileService.upload(file, path, user.getId()));
		else{//when file size more than 150mb,use chunked_upload
			Long offset = 0L;
			String uploadId = null;
			int chunk = 50*1024*1024;//every time upload size
			Map result = dropboxFileService.chunkedUpload(file,uploadId,offset,chunk, user.getId());
			while((offset=Long.parseLong(result.get("offset").toString()))<file.getSize()){
				uploadId = result.get("upload_id").toString();
				result = dropboxFileService.chunkedUpload(file, uploadId,offset,chunk, user.getId());
			}
			return WebResponse.success(dropboxFileService.commitChunkedUpload(path,uploadId,user.getId()));		
		}
	}
	
	@WebGet("/dropbox/share")
	public WebResponse share(@WebParam("path") String path,@WebUser User user){
		return WebResponse.success(dropboxFileService.share(path,user.getId()));
	}
	
	@WebPost("/dropbox/restore")
	public WebResponse restore(@WebParam("path") String path,@WebParam("rev")String rev,@WebUser User user){
		path = path.startsWith("/")?path:("/"+path);
		return WebResponse.success(dropboxFileService.restore(path,rev,user.getId()));
	}
	
	@WebGet("/dropbox/revisions")
	public WebResponse getRevisions(@WebParam("path") String path,@WebUser User user){
		path = path.startsWith("/")?path:("/"+path);
		return WebResponse.success(dropboxFileService.getRevisions(path,user.getId()));
	}
	
	@WebPost("/dropbox/copy")
	public WebResponse copy(@WebParam("fromPath") String fromPath,@WebParam("toPath") String toPath,@WebUser User user){
		return WebResponse.success(dropboxFileService.copy(fromPath,toPath,user.getId()));
	}
	
	@WebPost("/dropbox/move")
	public WebResponse move(@WebParam("fromPath") String fromPath,@WebParam("toPath") String toPath,@WebUser User user){
		return WebResponse.success(dropboxFileService.move(fromPath,toPath,user.getId()));
	}
	
	@WebGet("/dropbox/search")
	public WebResponse search(@WebParam("query") String query,@WebParam("path") String path,
			@WebParam("include_deleted") Boolean includeDeleted,@WebUser User user){
		if(includeDeleted==null)
			includeDeleted = false;
		return WebResponse.success(dropboxFileService.search(path, query, user.getId(), includeDeleted));
	}
	
	@WebGet("/dropbox/getMedia")
	public WebResponse getMedia(@WebParam("path") String path,@WebUser User user){
		return WebResponse.success(dropboxFileService.getMedia(path, user.getId()));
	}
	
	@WebGet("/dropbox/getDelta")
	public WebResponse getDelta(@WebParam("path") String path,@WebUser User user){
		return WebResponse.success(dropboxFileService.getDelta(path, user.getId()));
	}
}
