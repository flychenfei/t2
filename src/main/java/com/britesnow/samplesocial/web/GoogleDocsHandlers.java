package com.britesnow.samplesocial.web;


import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.britesnow.samplesocial.service.GoogleDocsService;
import com.britesnow.snow.util.Pair;
import com.britesnow.snow.web.RequestContext;
import com.britesnow.snow.web.param.annotation.WebParam;
import com.britesnow.snow.web.rest.annotation.WebGet;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class GoogleDocsHandlers {
    @Inject
    private GoogleDocsService googleDocListService;


    @WebGet("/googleDocsList/listDocs")
    public Object listFiles(@WebParam("pageIndex") String nextPagetoken,@WebParam("pageSize") Integer pageSize) throws Exception {
    	Pair<String, List<Map>> pair = googleDocListService.listFiles(nextPagetoken, pageSize);
		List<Map> docsInfo = pair.getSecond();
		WebResponse result = WebResponse.success(docsInfo);
		result.set("nextPageToken", pair.getFirst());
    	return result;
    }
    
    @WebGet("/googleDocsList/search")
    public Object searchFile(@WebParam("title") String title, @WebParam("pageIndex") String nextPagetoken,@WebParam("pageSize") Integer pageSize) throws Exception {
    	Pair<String, List<Map>> pair = googleDocListService.searchFile(title, nextPagetoken, pageSize);
		List<Map> docsInfo = pair.getSecond();
		WebResponse result = WebResponse.success(docsInfo);
		result.set("nextPageToken", pair.getFirst());
    	return result;
    }
    
    @WebGet("/googleDocsList/deleteDoc")
    public Object deleteFile(@WebParam("fileId") String fileId, @WebParam("etag") String etag, @WebParam("Permanent") Boolean Permanent) throws Exception {
        if(googleDocListService.trashFile(fileId, Permanent))
        	return WebResponse.success();
        else
        	return WebResponse.fail();
    }
    
    @WebGet("/googleDocsList/download")
    public void download(@WebParam("fileId") String fileId, @WebParam("fileName") String fileName,  RequestContext rc) throws Exception {
    	InputStream in = googleDocListService.download(fileId);
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
}
