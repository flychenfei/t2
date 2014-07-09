package com.britesnow.samplesocial.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;

import com.britesnow.snow.util.Pair;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpMediaType;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.MultipartContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.Drive.Files.Insert;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class GoogleDocsService {

    @Inject
    private GoogleAuthService authService;
    
    private static String MEDIATYPE = "https://www.googleapis.com/upload/drive/v2/files?uploadType=media";
    private static String MULTIPARTTYPE = "https://www.googleapis.com/upload/drive/v2/files?uploadType=multipart";
//    private static String RESUMABLETYPE = "https://www.googleapis.com/upload/drive/v2/files?uploadType=resumable";
    
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
				item.put("url", file.getDownloadUrl());
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
				if(file.getDownloadUrl() != null){
					item.put("hasUrl", "true");
				}
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
    
    public boolean uploadFile(FileItem fileItem){
    	if(fileItem.getSize() > 1000){
    		return multipartUploadFile(fileItem);
    	}
    	if(fileItem.getName() == null){
    		return simpleUploadFile(fileItem);
    	}
    	File metadata = new File();
    	metadata.setTitle(fileItem.getName());
    	metadata.setMimeType(fileItem.getContentType());
        try {
            InputStreamContent content = new InputStreamContent("application/vnd.google-apps.drive-sdk", fileItem.getInputStream());
            Insert insert = getDriverService().files().insert(metadata, content);
            insert.put("uploadType", "multipart");
            insert.execute();
          return true;
        } catch (IOException e) {
          e.printStackTrace();
          return false;
        }
    }
    
    /**
     * Just upload a media without metadata 
     * uploadType=media
     * @return
     */
    public boolean simpleUploadFile(FileItem fileItem){
    	
    	 try {
             InputStreamContent content = new InputStreamContent(fileItem.getContentType(), fileItem.getInputStream());
             Drive drive = getDriverService();
             GenericUrl url = new GenericUrl(MEDIATYPE);
             drive.getRequestFactory().buildPostRequest(url, content).execute();
           return true;
         } catch (IOException e) {
           e.printStackTrace();
           return false;
         }
    }
    
    /**
     * Upload a media with metadata 
     * uploadType=multipart
     * @return
     */
    public boolean multipartUploadFile(FileItem fileItem){

        try {
        	MultipartContent content = new MultipartContent();
        	content.setMediaType(new HttpMediaType("multipart/related"));
        	content.setBoundary("__END_OF_PART__");
        	//take file metadata with json
        	JsonFactory jsonFactory = new JacksonFactory();
        	Map<String,String> metadata = new HashMap<String,String>();
        	metadata.put("title", fileItem.getName());
        	JsonHttpContent jhc = new JsonHttpContent(jsonFactory, metadata);
        	jhc.setMediaType(new HttpMediaType("application/json"));
        	//file content
        	InputStreamContent fileContent = new InputStreamContent(fileItem.getContentType(), fileItem.getInputStream());
        	//add MultipartContent
        	content.addPart(new MultipartContent.Part(jhc));
        	content.addPart(new MultipartContent.Part(fileContent));
        	
            Drive drive = getDriverService();
            GenericUrl url = new GenericUrl(MULTIPARTTYPE);
            drive.getRequestFactory().buildPostRequest(url, content).execute();
          return true;
        } catch (IOException e) {
          e.printStackTrace();
          return false;
        }
    }
    
    /**
     * Move the file to the trash
     * @param fileId
     * @param Permanent
     * @return
     */
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
    
    /**
     * Permanently delete the file, skipping the trash
     * @param fileId
     * @return
     */
    public boolean deleteFile(String fileId){
    	Drive service = getDriverService();
		try {
		      service.files().delete(fileId).execute();
		    } catch (IOException e) {
		      e.printStackTrace();
		}
        return true;
    }
    
    public InputStream download(String fileId){
    	File file = getFile(fileId);
    	if (file.getDownloadUrl() != null && file.getDownloadUrl().length() > 0) {
    	      try {
    	        HttpResponse resp = getDriverService().getRequestFactory().buildGetRequest(new GenericUrl(file.getDownloadUrl())).execute();
    	        return resp.getContent();
    	      } catch (IOException e) {
    	        e.printStackTrace();
    	        return null;
    	      }
    	    } else {
    	      return null;
    	    }
	}
    
    private File getFile(String fileId) {
        try {
          File file = getDriverService().files().get(fileId).execute();
          return file;
        } catch (IOException e) {
          e.printStackTrace();
        }
        return null;
      }
    
    private Drive getDriverService(){
        HttpTransport httpTransport = new NetHttpTransport();
        JacksonFactory jsonFactory = new JacksonFactory();
        GoogleCredential credential = new GoogleCredential().setAccessToken(authService.getSocialIdEntity().getToken());
        Drive service = new Drive.Builder(httpTransport, jsonFactory, credential).setApplicationName("Drive Test").build();
        return service;
    }
}
