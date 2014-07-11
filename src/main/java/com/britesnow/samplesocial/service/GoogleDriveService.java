package com.britesnow.samplesocial.service;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
public class GoogleDriveService {

    @Inject
    private GoogleAuthService authService;
    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    private static String MEDIATYPE = "https://www.googleapis.com/upload/drive/v2/files?uploadType=media";
    private static String MULTIPARTTYPE = "https://www.googleapis.com/upload/drive/v2/files?uploadType=multipart";
    
    public Pair<String, List<Map>> searchFile(String queryString,String nextPageToken, Integer pageSize){
    	return searchFiles(nextPageToken, pageSize, queryString);
    }
    
    public Pair<String, List<Map>> list(String nextPageToken, Integer pageSize, boolean trash){
    	return searchFiles(nextPageToken, pageSize, "trashed="+trash);
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
     * 
     * @param fileId
     * @return
     */
    public boolean trashFile(String fileId){
    	Drive service = getDriverService();
		try {
		      service.files().trash(fileId).execute();
		    } catch (IOException e) {
		     e.printStackTrace();
		}
        return true;
    }
    
    /**
     * Restore a file from the trash.
     * 
     * @param fileId
     * @return
     */
    public boolean untrashFile(String fileId){
    	Drive service = getDriverService();
		try {
		      service.files().untrash(fileId).execute();
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
    
    /**
     * search files based on the queryString ,see https://developers.google.com/drive/web/search-parameters
     * @param nextPageToken
     * @param pageSize
     * @param queryString
     * @return
     */
    private Pair<String, List<Map>> searchFiles(String nextPageToken,Integer pageSize,String queryString){
    	List<Map> results = new ArrayList<Map>();
    	if(nextPageToken != null && nextPageToken.equals("lastPage")){
    		return new Pair<String, List<Map>>("lastPage", results);
    	}
    	Files.List request = null;
    	FileList filelist = null;
        try {
        	request = getDriverService().files().list();
			if(nextPageToken != null && !nextPageToken.equals("") && !nextPageToken.equals("0")){
				request.setPageToken(nextPageToken);
			}
			request.setMaxResults(pageSize);
			request.setQ(queryString);
        	filelist = request.execute();
			results = formatFiles(filelist.getItems());
		} catch (IOException e) {
			e.printStackTrace();
		}
        return new Pair<String, List<Map>>(filelist.getNextPageToken(), results);
    }

    /**
     * format the files object, like date format
     * @param files
     * @return
     */
    private List<Map> formatFiles(List<File> files){
    	List<Map> results = new ArrayList<Map>();
    	for(File file : files){
			Map<String, String> item = new HashMap<String, String>();
			item.put("fileId", file.getId());
			item.put("fileName", file.getTitle());
			item.put("createTime", df.format(new Date(file.getCreatedDate().getValue())));
			item.put("updateTime", df.format(new Date(file.getModifiedDate().getValue())));
			item.put("fileType", file.getMimeType());
			item.put("url", file.getDownloadUrl());
			if(file.getFileSize() != null){
				item.put("fileSize", String.valueOf(file.getFileSize()));
			}else{
				item.put("fileSize", "NO Size");
			}
			if(file.getDownloadUrl() != null){
				item.put("hasUrl", "true");
			}
			item.put("owner", file.getOwnerNames().get(0));
			item.put("etag", file.getEtag());
			results.add(item);
		}
    	return results;
    }
}
