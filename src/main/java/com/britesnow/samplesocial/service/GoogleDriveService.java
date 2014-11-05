package com.britesnow.samplesocial.service;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.fileupload.FileItem;

import com.britesnow.samplesocial.util.GoogleDriveDataPack;
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
import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.google.api.client.util.DateTime;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.Drive.Files.Insert;
import com.google.api.services.drive.model.About;
import com.google.api.services.drive.model.ChildReference;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.File.Labels;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.ParentList;
import com.google.api.services.drive.model.ParentReference;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class GoogleDriveService {

    @Inject
    private GoogleAuthService authService;
    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    private static String MEDIATYPE = "https://www.googleapis.com/upload/drive/v2/files?uploadType=media";
    private static String MULTIPARTTYPE = "https://www.googleapis.com/upload/drive/v2/files?uploadType=multipart";
    
    public Pair<String, List<Map>> searchFile(String keyword, String searchType, String startDate,String endDate,String nextPageToken, Integer pageSize, Boolean starred){
    	StringBuilder query = new StringBuilder();
    	if(starred != null){
    	    query.append("starred=").append(starred);
    	}else{
    	    if(!Strings.isNullOrEmpty(keyword)){
                query.append(searchType).append(" contains '").append(keyword).append("'");
            }
            if(!Strings.isNullOrEmpty(startDate)){
                if(query.length() != 0){
                    query.append(" and");
                }
                query.append(" modifiedDate >= '").append(formDate(startDate,true)).append("'");
            }
            if(!Strings.isNullOrEmpty(endDate)){
                if(query.length() != 0){
                    query.append(" and");
                }
                query.append(" modifiedDate <= '").append(formDate(endDate,false)).append("'");
            }
    	}
    	
		
    	return searchFiles(nextPageToken, pageSize, query.toString());
    }
    /**
     * list files 
     * 
     * @param selfId
     * @param nextPageToken
     * @param pageSize
     * @param trash
     * @return
     */
    public GoogleDriveDataPack list(String selfId, String nextPageToken, Integer pageSize, boolean trash){
    	StringBuilder query = new StringBuilder();
    	query.append("trashed=").append(trash);
    	if(!Strings.isNullOrEmpty(selfId)){
        	query.append(" and '").append(selfId).append("' in parents");
    	}else{
    		query.append(" and 'root' in parents");
    	}
    	Pair<String, List<Map>> pair = searchFiles(nextPageToken, pageSize, query.toString());
    	Map<String,Object> generalData = new HashMap<String,Object>();
    	generalData.put("next", pair.getFirst());
    	if(!Strings.isNullOrEmpty(selfId)){
    		generalData.put("parentId", selfId);
    	}else if(pair.getSecond() != null && pair.getSecond().size() > 0 && pair.getSecond().get(0).get("parentId") != null){
    		generalData.put("parentId", pair.getSecond().get(0).get("parentId").toString());
    	}
    	return new GoogleDriveDataPack(generalData, pair.getSecond());
    }

    /**
     * list Previous parent files
     * 
     * @param selfId
     * @param pageSize
     * @return
     */
    public GoogleDriveDataPack listParent(String selfId,Integer pageSize){
    	GoogleDriveDataPack results = null;
    	String parentId = getParentId(selfId);
    	if(Strings.isNullOrEmpty(parentId)){
    		results = list(selfId, null, pageSize, false);
    		results.getGeneralData().put("previous", false);
    	}else{
    		results = list(parentId, null, pageSize, false);
    		results.getGeneralData().put("previous", true);
    	}
    	return results;
    }
    
    /**
     * upload file
     * 
     * @param parentId
     * @param fileItem
     * @return
     */
    public boolean uploadFile(String parentId, FileItem fileItem){
    	if(fileItem.getSize() > 2000){
    		return multipartUploadFile(parentId, fileItem);
    	}
    	if(fileItem.getName() == null){
    		return simpleUploadFile(parentId, fileItem);
    	}
    	File metadata = new File();
    	metadata.setTitle(fileItem.getName());
    	metadata.setMimeType(fileItem.getContentType());
    	metadata.setParents(Arrays.asList(new ParentReference().setId(parentId)));
        try {
            InputStreamContent content = new InputStreamContent(fileItem.getContentType(), fileItem.getInputStream());
            Insert insert = getDriverService().files().insert(metadata, content);
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
    public boolean simpleUploadFile(String parentId, FileItem fileItem){
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
    public boolean multipartUploadFile(String parentId, FileItem fileItem){
        try {
        	MultipartContent content = new MultipartContent();
        	content.setMediaType(new HttpMediaType("multipart/related"));
        	content.setBoundary("__END_OF_PART__");
        	//take file metadata with json
        	JsonFactory jsonFactory = new JacksonFactory();
        	Map<String,Object> metadata = new HashMap<String,Object>();
        	metadata.put("title", fileItem.getName());
        	List<Map> parents = new ArrayList<Map>();
        	Map parent = new HashMap<String,String>();
        	parent.put("id", parentId);
        	parents.add(parent);
        	metadata.put("parents", parents);
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
    public boolean createFolder (String parentId, String folderName){
        try {
        	File file = new File();
        	file.setTitle(folderName);
        	file.setMimeType("application/vnd.google-apps.folder");
        	file.setParents(Arrays.asList(new ParentReference().setId(parentId)));
            Insert insert = getDriverService().files().insert(file);
            insert.execute();
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
     * Restore a file from the trash.
     * 
     * @param fileId
     * @return
     */
    public boolean copyFile(String fileId, String parentId, String copyTitle, String targetId){
    	if(Strings.isNullOrEmpty(fileId) || Strings.isNullOrEmpty(parentId) || Strings.isNullOrEmpty(targetId))
    		return false;
    	Drive service = getDriverService();
		try {
			  File copiedFile = new File();
			  copiedFile.setTitle(copyTitle);
			  File file =  service.files().copy(fileId, copiedFile).execute();
			  if(!Strings.isNullOrEmpty(copyTitle)){
				  patchFile(file.getId(),copyTitle,null);
			  }
			  if(targetId.equals("root")){
				  targetId = userInfo().get("rootFolderId").toString();
		      }
			  if(!parentId.equals(targetId)){
				  if(moveFile(file.getId(), parentId, targetId)){
					  return true;
				  }else {
					  service.files().delete(file.getId()).execute();
					  return false;
				  }
			  }
			  return true;
		    } catch (IOException e) {
		     e.printStackTrace();
		     return false;
		}
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
    
    /**
     * Permanently deletes all of the user's trashed files
     * 
     * @return
     */
    public boolean emptyTrash(){
    	Drive service = getDriverService();
		try {
		      service.files().emptyTrash().execute();
		    } catch (IOException e) {
		     e.printStackTrace();
		     return false;
		}
        return true;
    }
    
    /**
     * restore all of the user's trashed files
     * 
     * @return
     */
	public boolean restoreTrash() {
		Pair<String,List<File>> pair = null;
		List<File> results = null;
		String nextPagetoken = null;
		do {
			pair = ListFiles(nextPagetoken,100,true);
			nextPagetoken = pair.getFirst();
			results = pair.getSecond();
			for(File file:results){
				untrashFile(file.getId());
			}
		} while (!Strings.isNullOrEmpty(nextPagetoken));
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
  
    /**
     * get a file instance by fileId
     * 
     * @param fileId
     * @return
     */
    public boolean patchFile(String fileId, String title, String description) {
    	try {
    	      File file = new File();
    	      file.setTitle(title);
    	      if(!Strings.isNullOrEmpty(description)){
        	      file.setDescription(description);
    	      }
    	      Files.Patch patchRequest = getDriverService().files().patch(fileId, file);
    	      patchRequest.execute();
    	      return true;
    	    } catch (IOException e) {
    	      e.printStackTrace();
    	      return false;
    	    }
      }
    
    /**
     * Update a file's modified date.
     * 
     * @param fileId
     * @return
     */
    public boolean touchFile(String fileId) {
    	try {
    	      getDriverService().files().touch(fileId).execute();
    	      return true;
    	    } catch (IOException e) {
    	      e.printStackTrace();
    	      return false;
    	    }
      }
    
    public List<Map> foldersInfo(String currentId){
    	if(Strings.isNullOrEmpty(currentId)){
    		currentId = userInfo().get("rootFolderId").toString();
    	}
    	List<Map> results = new ArrayList<Map>();
		List<File> items = getFolderList(currentId);
		for(File file:items){
			Map<String,Object> item = new HashMap<String,Object>();
			item.put("name", file.getTitle());
			item.put("selfId", file.getId());
			List<File> childs = getFolderList(file.getId());
			if(childs != null && childs.size() > 0){
				item.put("hasChild", true);
			}else{
				item.put("hasChild", false);
			}
			results.add(item);
		}
    	return results;
    }
    
    public boolean moveFile(String fileId, String parentId, String moveToId){
    	if(Strings.isNullOrEmpty(fileId) || Strings.isNullOrEmpty(parentId) || Strings.isNullOrEmpty(moveToId))
    		return false;
    	if(moveToId.equals("root")){
    		moveToId = userInfo().get("rootFolderId").toString();
    	}
    	if(parentId.equals(moveToId))
    		return false;
    	if(insertChildren(moveToId, fileId)){
    		if(deleteChildren(parentId, fileId)){
        		return true;
        	}else{
        		deleteChildren(moveToId, fileId);
        	}
    	}
    	return false;
    }
    
    
    public boolean updateStarred(String fileId, Boolean starred) {
        // TODO Auto-generated method stub
        try {
            Drive service = getDriverService();
            File file = service.files().get(fileId).execute();
            Labels labels = new Labels();
            if(starred){
                labels.setStarred(starred);
            }else{
                labels.setStarred(starred);
            }
            file.setLabels(labels);
            
            File updatedFile = service.files().update(fileId, file).execute();
            return true;
        } catch (IOException e) {
          e.printStackTrace();
          return false;
        }
    }
    
    /**
     * information about the current user
     * 
     * @return
     */
    private Map userInfo(){
    	Map<String,String> useInfo = new HashMap<String,String>();
    	try {
			About about = getDriverService().about().get().execute();
			useInfo.put("userName", about.getName());
			useInfo.put("rootFolderId", about.getRootFolderId());
			return useInfo;
		} catch (IOException e) {
			e.printStackTrace();
			return useInfo;
		}
    }
    
    /**
     * Insert a file into a folder.
     * 
     * @param folderId
     * @param fileId
     * @return
     */
    private boolean insertChildren(String folderId,String fileId){
    	ChildReference newChild = new ChildReference();
        newChild.setId(fileId);
        try {
			getDriverService().children().insert(folderId, newChild).execute();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
    }
    
    /**
     * Insert a file into a folder.
     * 
     * @param folderId
     * @param fileId
     * @return
     */
    private boolean deleteChildren(String folderId,String fileId){
        try {
			getDriverService().children().delete(folderId, fileId).execute();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
    }
    
    private List<File> getFolderList(String parentId){
    	StringBuilder query = new StringBuilder();
    	query.append("trashed= false and '").append(parentId).append("' in parents and mimeType = 'application/vnd.google-apps.folder'");
    	try {
			Files.List request = getDriverService().files().list();
			request.setQ(query.toString());
			FileList filelist = request.execute();
			return filelist.getItems();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
    }
    
    /**
     * get [first] parentId by fileId
     * 
     * @param selfId
     * @return
     */
    private String getParentId(String selfId){
    	try {
    	      ParentList parents = getDriverService().parents().list(selfId).execute();
    	      for (ParentReference parent : parents.getItems()) {
    	        return parent.getId();
    	      }
    	    } catch (IOException e) {
    	      return null;
    	    }
		return null;
    }
    
    /**
     * get a file instance by fileId
     * 
     * @param fileId
     * @return
     */
    private File getFile(String fileId) {
        try {
          File file = getDriverService().files().get(fileId).execute();
          return file;
        } catch (IOException e) {
          e.printStackTrace();
        }
        return null;
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
     * search files based on the queryString ,see https://developers.google.com/drive/web/search-parameters
     * 
     * @param nextPageToken
     * @param pageSize
     * @param queryString
     * @return
     */
    private Pair<String,List<File>> ListFiles(String nextPageToken,Integer pageSize,boolean istrash){
    	Files.List request = null;
    	FileList filelist = null;
        try {
        	request = getDriverService().files().list();
			if(!Strings.isNullOrEmpty(nextPageToken) && !nextPageToken.equals("0")){
				request.setPageToken(nextPageToken);
			}
			request.setMaxResults(pageSize);
			if(istrash){
				request.setQ("trashed = true");
			}else{
				request.setQ("trashed = false");
			}
        	filelist = request.execute();
			return new Pair<String,List<File>>(filelist.getNextPageToken(),filelist.getItems());
		} catch (IOException e) {
			e.printStackTrace();
	        return null;
		}
    }
    /**
     * format the files object, like date format
     * @param files
     * @return
     */
    private List<Map> formatFiles(List<File> files){
    	return files.stream().map(file->{
            Map<String, Object> item = new HashMap<>();
            item.put("fileId", file.getId());
            item.put("iconLink", file.getIconLink());
            item.put("fileName", file.getTitle());
            item.put("createTime", df.format(new Date(file.getCreatedDate().getValue())));
            item.put("updateTime", df.format(new Date(file.getModifiedDate().getValue())));
            item.put("mimeType", file.getMimeType());
            item.put("url", file.getDownloadUrl());
            item.put("starred", file.getLabels().getStarred());
            if(file.getFileSize() != null){
                item.put("fileSize", String.valueOf(file.getFileSize()));
            }else{
                item.put("fileSize", "NO Size");
            }
            if(file.getDownloadUrl() != null){
                item.put("hasUrl", "true");
            }
            List<ParentReference> parentReferenceList = file.getParents();
            if(parentReferenceList.size() != 0){
                item.put("parentId", parentReferenceList.get(0).getId());
            }
            item.put("owner", file.getOwnerNames().get(0));
            item.put("etag", file.getEtag());
            return item;
        }).collect(Collectors.toList());
    }
    
    private String formDate(String dateString, boolean isStart){
    	if(isStart){
    		dateString = dateString +" 00:00:00";
    	}else{
    		dateString = dateString +" 23:59:59";
    	}
    	Date date = null;
    	try {
			date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
			return dateString;
		}
    	DateTime dateTime = new DateTime(date);
		return dateTime.toStringRfc3339();
    }
    
    private Drive getDriverService(){
        HttpTransport httpTransport = new NetHttpTransport();
        JacksonFactory jsonFactory = new JacksonFactory();
        GoogleCredential credential = new GoogleCredential().setAccessToken(authService.getSocialIdEntity().getToken());
        Drive service = new Drive.Builder(httpTransport, jsonFactory, credential).setApplicationName("Drive Test").build();
        return service;
    }
    
  

}
