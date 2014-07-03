package com.britesnow.samplesocial.web;


import java.util.List;
import java.util.Map;

import com.britesnow.samplesocial.service.GoogleDocsService;
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
    public Object listFiles(@WebModel Map m, @WebParam("pageIndex") Integer pageIndex,@WebParam("pageSize") Integer pageSize) throws Exception {
        List<Map> results = googleDocListService.listFiles(pageIndex, pageSize); 
    	return WebResponse.success(results).set("result_count", results.size());
    }
    
}
