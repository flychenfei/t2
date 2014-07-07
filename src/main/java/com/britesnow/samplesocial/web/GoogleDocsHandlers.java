package com.britesnow.samplesocial.web;


import java.util.List;
import java.util.Map;

import com.britesnow.samplesocial.service.GoogleDocsService;
import com.britesnow.snow.util.Pair;
import com.britesnow.snow.web.param.annotation.WebModel;
import com.britesnow.snow.web.param.annotation.WebParam;
import com.britesnow.snow.web.rest.annotation.WebGet;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class GoogleDocsHandlers {
    @Inject
    private GoogleDocsService googleDocListService;


    @WebGet("/googleDocsList/listDocs")
    public Object listFiles(@WebModel Map m, @WebParam("pageIndex") String nextPagetoken,@WebParam("pageSize") Integer pageSize) throws Exception {
    	Pair<String, List<Map>> pair = googleDocListService.listFiles(nextPagetoken, pageSize);
		List<Map> docsInfo = pair.getSecond();
		WebResponse result = WebResponse.success(docsInfo);
		result.set("nextPageToken", pair.getFirst());
    	return result;
    }
    
    @WebGet("/googleDocsList/search")
    public Object searchFile(@WebModel Map m, @WebParam("title") String title, @WebParam("pageIndex") String nextPagetoken,@WebParam("pageSize") Integer pageSize) throws Exception {
    	Pair<String, List<Map>> pair = googleDocListService.searchFile(title, nextPagetoken, pageSize);
		List<Map> docsInfo = pair.getSecond();
		WebResponse result = WebResponse.success(docsInfo);
		result.set("nextPageToken", pair.getFirst());
    	return result;
    }
    
    @WebGet("/googleDocsList/deleteDoc")
    public Object deleteFile(@WebModel Map m, @WebParam("fileId") String fileId, @WebParam("etag") String etag, @WebParam("Permanent") Boolean Permanent) throws Exception {
        if(googleDocListService.trashFile(fileId, Permanent))
        	return WebResponse.success();
        else
        	return WebResponse.fail();
    }
}
