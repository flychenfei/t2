package com.britesnow.samplesocial.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.InputStreamEntity;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Verb;

import com.britesnow.samplesocial.entity.SocialIdEntity;
import com.britesnow.snow.util.JsonUtil;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.RESTUtility;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;
import com.dropbox.client2.session.Session.ProxyInfo;
import com.dropbox.client2.session.WebAuthSession;
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
	private static String SHARE = "https://api.dropbox.com/1/shares/dropbox";
	private static String RESTORE = "https://api.dropbox.com/1/restore/dropbox";
	private static String REVISIONS="https://api.dropbox.com/1/revisions/dropbox";
	private static String COPY = "https://api.dropbox.com/1/fileops/copy";
	private static String MOVE = "https://api.dropbox.com/1/fileops/move";
	private static String SEARCH = "https://api.dropbox.com/1/search/dropbox";
	private static String MEDIA = "https://api.dropbox.com/1/media/dropbox";
	private static String COPYREF = "https://api.dropbox.com/1/copy_ref/dropbox";
	private static String DELTA = "https://api.dropbox.com/1/delta";
	private static String COMMITCHUNKEDUPLOAD="https://api-content.dropbox.com/1/commit_chunked_upload/dropbox";
	
	public Map getMetadata(String path,Long userId,boolean includeDeleted,Locale locale){
		OAuthRequest request = new OAuthRequest(Verb.GET,METADATA+path);
		dropboxAuthService.setAuthorizationHeader(request, userId);
		request.addQuerystringParameter("locale", locale.toString());
		request.addQuerystringParameter("include_deleted", includeDeleted+"");
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
    	return JsonUtil.toMapAndList(metadata);
	}
	
	public Map delete(String path,Long userId){
		OAuthRequest request = new OAuthRequest(Verb.POST,DELETE);
		dropboxAuthService.setAuthorizationHeader(request, userId);
		request.addBodyParameter("path", path);
		request.addBodyParameter("root", "dropbox");
    	String metadata = request.send().getBody();
    	return JsonUtil.toMapAndList(metadata);
	}
	
	public Entry upload(FileItem item,String path,Long userId) throws IOException, DropboxException{
		WebAuthSession session = dropboxAuthService.getWebAuthSession();
	    SocialIdEntity soId = dropboxAuthService.getSocialIdEntity(userId);
	    AccessTokenPair accessPair = new AccessTokenPair(soId.getToken(),soId.getSecret());
	    session.setAccessTokenPair(accessPair);
	    DropboxAPI<WebAuthSession> dropboxApi= new DropboxAPI<WebAuthSession>(session);
    	return dropboxApi.putFile(path, item.getInputStream(),item.getSize(), null, null);
	}
	
	public Map share(String path,Long userId){
		OAuthRequest request = new OAuthRequest(Verb.POST,SHARE+path);
		dropboxAuthService.setAuthorizationHeader(request, userId);
    	String metadata = request.send().getBody();
    	return JsonUtil.toMapAndList(metadata);
	}
	
	public Map restore(String path,String rev,Long userId){
		OAuthRequest request = new OAuthRequest(Verb.POST,RESTORE+path);
		dropboxAuthService.setAuthorizationHeader(request, userId);
		request.addBodyParameter("rev", rev);
    	String metadata = request.send().getBody();
    	return JsonUtil.toMapAndList(metadata);
	}
	
	public String getRevisions(String path,Long userId){
		OAuthRequest request = new OAuthRequest(Verb.GET,REVISIONS+path);
		dropboxAuthService.setAuthorizationHeader(request, userId);
    	String revisions = request.send().getBody();
    	return revisions;
	}
	
	public Map copy(String fromPath,String toPath,Long userId){
		OAuthRequest request = new OAuthRequest(Verb.POST,COPY);
		dropboxAuthService.setAuthorizationHeader(request, userId);
		request.addBodyParameter("root", "dropbox");
		request.addBodyParameter("from_path", fromPath);
		request.addBodyParameter("to_path", toPath+fromPath);
    	String copyResult = request.send().getBody();
    	return JsonUtil.toMapAndList(copyResult);
	}
	
	public Map move(String fromPath,String toPath,Long userId){
		OAuthRequest request = new OAuthRequest(Verb.POST,MOVE);
		dropboxAuthService.setAuthorizationHeader(request, userId);
		request.addBodyParameter("root", "dropbox");
		request.addBodyParameter("from_path", fromPath);
		request.addBodyParameter("to_path", toPath+fromPath);
    	String copyResult = request.send().getBody();
    	return JsonUtil.toMapAndList(copyResult);
	}
	
	public String search(String path,String query,Long userId,boolean includeDeleted){
		OAuthRequest request = new OAuthRequest(Verb.GET,SEARCH+path);
		dropboxAuthService.setAuthorizationHeader(request, userId);
		request.addQuerystringParameter("include_deleted", includeDeleted+"");
		request.addQuerystringParameter("query", query);
    	String searchResult = request.send().getBody();
    	return searchResult;
	}
	
	public Map getMedia(String path,Long userId){
		OAuthRequest request = new OAuthRequest(Verb.POST,MEDIA+path);
		dropboxAuthService.setAuthorizationHeader(request, userId);
    	String media = request.send().getBody();
    	return JsonUtil.toMapAndList(media);
    	
	}
	
	public Map getCopyref(String path,Long userId){
		OAuthRequest request = new OAuthRequest(Verb.POST,COPYREF+path);
		dropboxAuthService.setAuthorizationHeader(request, userId);
    	String media = request.send().getBody();
    	return JsonUtil.toMapAndList(media);
	}
	
	public Map getDelta(String path,Long userId){
		OAuthRequest request = new OAuthRequest(Verb.POST,DELTA);
		dropboxAuthService.setAuthorizationHeader(request, userId);
    	String delta = request.send().getBody();
    	return JsonUtil.toMapAndList(delta);
	}
	
	public Map chunkedUpload(FileItem item,String uploadId,Long offset,int chunk,Long userId) throws IOException, DropboxException{
		WebAuthSession session = dropboxAuthService.getWebAuthSession();
		String url = RESTUtility.buildURL(session.getContentServer(), 1,
	                "/chunked_upload", new String[]{"upload_id", uploadId, "offset", ""+offset});
		HttpPut req = new HttpPut(url);
	    SocialIdEntity soId = dropboxAuthService.getSocialIdEntity(userId);
	    AccessTokenPair accessPair = new AccessTokenPair(soId.getToken(),soId.getSecret());
	    session.setAccessTokenPair(accessPair);
		session.sign(req);
		InputStream in = item.getInputStream();
		byte[] data = new byte[chunk];
		in.skip(offset);
		int length = in.read(data);
		InputStreamEntity ise = new InputStreamEntity(new ByteArrayInputStream(data,0,length),length);
		ise.setContentEncoding("application/octet-stream");
		ise.setChunked(false);
		req.setEntity(ise);
		HttpClient client = session.getHttpClient();
		ProxyInfo proxyInfo = session.getProxyInfo();
		if (proxyInfo != null && proxyInfo.host != null&&!proxyInfo.host.equals("")) {
			HttpHost proxy;
			if (proxyInfo.port < 0) {
				proxy = new HttpHost(proxyInfo.host);
			} else {
				proxy = new HttpHost(proxyInfo.host, proxyInfo.port);
			}
			client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,proxy);
		} else {
			client.getParams().removeParameter(ConnRoutePNames.DEFAULT_PROXY);
		}
		HttpResponse rep = client.execute(req);
		in.close();
    	return JsonUtil.toMapAndList(RESTUtility.parseAsJSON(rep).toString());
	}
	
	public Object commitChunkedUpload(String path,String uploadId,Long userId) throws IOException, DropboxException{
		OAuthRequest request = new OAuthRequest(Verb.POST,COMMITCHUNKEDUPLOAD+path);
		dropboxAuthService.setAuthorizationHeader(request, userId);
		request.addBodyParameter("upload_id", uploadId);
    	String commitChunkedUpload = request.send().getBody();
    	return JsonUtil.toMapAndList(commitChunkedUpload);
	}
	
	
}
