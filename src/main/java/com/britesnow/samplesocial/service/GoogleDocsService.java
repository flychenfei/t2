package com.britesnow.samplesocial.service;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gdata.client.DocumentQuery;
import com.google.gdata.client.docs.DocsService;
import com.google.gdata.data.docs.DocumentListEntry;
import com.google.gdata.data.docs.DocumentListFeed;
import com.google.gdata.util.ServiceException;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class GoogleDocsService {

    @Inject
    private GoogleAuthService authService;
    
    private final String BASE_DOCS_URL = "https://docs.google.com/feeds/default/private/full?v=3";
    
    public List<Map> listFiles(Integer pageIndex, Integer pageSize){
    	List<Map> results = null;
    	try {
    		URL feedUrl = new URL(BASE_DOCS_URL);
    		DocsService docsService = getDocsService();
        	DocumentQuery query = new DocumentQuery(feedUrl);
        	query.setStartIndex(pageIndex+1);
        	query.setMaxResults(pageSize);
        	DocumentListFeed feed = docsService.query(query, DocumentListFeed.class);
        	results = new ArrayList<Map>();
        	Map<String,String> item = null;
			for (DocumentListEntry entry : feed.getEntries()) {
				item = new HashMap<String,String>();
				item.put("name", entry.getTitle().getPlainText());
				item.put("createTime", entry.getUpdated().toString());
				item.put("type", entry.getType());
				results.add(item);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ServiceException e) {
			e.printStackTrace();
		}
        return results;
    }   
    
    private DocsService getDocsService() {
    	DocsService docsService = new DocsService("MyDocumentsListIntegration-v1");
    	docsService.setAuthSubToken(authService.getSocialIdEntity().getToken(), null);
        return docsService;
    }
}
