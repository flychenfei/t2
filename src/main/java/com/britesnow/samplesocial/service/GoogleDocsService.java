package com.britesnow.samplesocial.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.britesnow.snow.util.Pair;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class GoogleDocsService {

    @Inject
    private GoogleAuthService authService;
    
    
    public Pair<String, List<Map>> searchFile(String title,String nextPagetoken, Integer pageSize){
    	List<Map> results = new ArrayList<Map>();
    	if(nextPagetoken != null && nextPagetoken.equals("lastPage"))
    		return new Pair<String, List<Map>>("lastPage", results);
    	Map<String,String> item = null;
    	Files.List request = null;
    	FileList filelist = null;
    	StringBuilder query = new StringBuilder();
        try {
        	request = getDriverService().files().list();
			if(nextPagetoken != null && !nextPagetoken.equals("") && !nextPagetoken.equals("0"))
				request.setPageToken(nextPagetoken);
			request.setMaxResults(pageSize);
			query.append("trashed = false");
			if(title != null && !title.equals("")){
				query.append("and title = '").append(title).append("'");
				request.setQ(query.toString());
			}else{
				return new Pair<String, List<Map>>("lastPage", results);
			}
        	filelist = request.execute();
			List<File> files = filelist.getItems();
			for(File file : files){
				System.out.println(file);
				item = new HashMap<String, String>();
				item.put("fileId", file.getId());
				item.put("fileName", file.getTitle());
				item.put("createTime", file.getCreatedDate().toString());
				item.put("updateTime", file.getModifiedDate().toString());
				item.put("fileType", file.getMimeType());
				if(file.getFileSize() != null){
					item.put("fileSize", String.valueOf(file.getFileSize()));
				}else{
					item.put("fileSize", "NO Size");
				}
				item.put("owner", file.getOwnerNames().get(0));
				item.put("etag", file.getEtag());
				results.add(item);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
        return new Pair<String, List<Map>>(filelist.getNextPageToken(), results);
    }
    
    //list file not trashed
    public Pair<String, List<Map>> listFiles(String nextPagetoken, Integer pageSize){
    	List<Map> results = new ArrayList<Map>();
    	if(nextPagetoken != null && nextPagetoken.equals("lastPage"))
    		return new Pair<String, List<Map>>("lastPage", results);
    	Map<String,String> item = null;
    	Files.List request = null;
    	FileList filelist = null;
        try {
        	request = getDriverService().files().list();
			if(nextPagetoken != null && !nextPagetoken.equals("") && !nextPagetoken.equals("0"))
				request.setPageToken(nextPagetoken);
			request.setMaxResults(pageSize);
			request.setQ("trashed = false");
        	filelist = request.execute();
			List<File> files = filelist.getItems();
			for(File file : files){
				item = new HashMap<String, String>();
				item.put("fileId", file.getId());
				item.put("fileName", file.getTitle());
				item.put("createTime", file.getCreatedDate().toString());
				item.put("updateTime", file.getModifiedDate().toString());
				item.put("fileType", file.getMimeType());
				if(file.getFileSize() != null){
					item.put("fileSize", String.valueOf(file.getFileSize()));
				}else{
					item.put("fileSize", "NO Size");
				}
				item.put("owner", file.getOwnerNames().get(0));
				item.put("etag", file.getEtag());
				results.add(item);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
        return new Pair<String, List<Map>>(filelist.getNextPageToken(), results);
    }
    
    //Move a file to the trash.
    public boolean trashFile(String fileId,  boolean Permanent){
    	if(Permanent){
    		return deleteFile(fileId);
    	}
    	Drive service = getDriverService();
		try {
		      service.files().trash(fileId).execute();
		    } catch (IOException e) {
		     e.printStackTrace();
		}
        return true;
    }
    
    //Permanently delete a file, skipping the trash.
    public boolean deleteFile(String fileId){
    	Drive service = getDriverService();
		try {
		      service.files().delete(fileId).execute();
		    } catch (IOException e) {
		      e.printStackTrace();
		}
        return true;
    }
    
    private Drive getDriverService(){
        HttpTransport httpTransport = new NetHttpTransport();
        JacksonFactory jsonFactory = new JacksonFactory();
        GoogleCredential credential = new GoogleCredential().setAccessToken(authService.getSocialIdEntity().getToken());
        Drive service = new Drive.Builder(httpTransport, jsonFactory, credential).setApplicationName("Drive Test").build();
        return service;
    }
}
