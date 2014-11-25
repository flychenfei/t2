package com.britesnow.samplesocial.service;

import com.britesnow.snow.util.JsonUtil;
import com.google.gdata.util.common.base.StringUtil;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Verb;

import java.io.InputStream;
import java.util.Map;


@Singleton
public class LiveDriveService {
    @Inject
	private LiveAuthService oAuthService;

    public static final String FOLDERLIST_ENDPOINT = "https://apis.live.net/v5.0/me/skydrive";
    public static final String LIVE_ENDPOINT = "https://apis.live.net/v5.0";

    /**
     * get user root folder (onedrive)
     */
    public Map getRootFolder() {
        OAuthRequest  request = oAuthService.createRequest(Verb.GET, FOLDERLIST_ENDPOINT);
        Response response = request.send();
        Map profile = JsonUtil.toMapAndList(response.getBody());
        return profile;
    }

    /**
     * get the files under the folder or album
     */
    public Map getFolderFilesList(String folderId) {
        OAuthRequest request = oAuthService.createRequest(Verb.GET, LIVE_ENDPOINT + "/" + folderId + "/files");
        Response response = request.send();
        Map profile = JsonUtil.toMapAndList(response.getBody());
        return profile;
    }

    /**
     * get the object by id (folder, album, file and photo)
     */
    public Map get(String id) {
        OAuthRequest request = oAuthService.createRequest(Verb.GET, LIVE_ENDPOINT + "/" + id);
        Response response = request.send();
        Map info = JsonUtil.toMapAndList(response.getBody());
        return info;
    }

    /**
     * delete the object by id (folder, album, file and photo)
     */
    public void delete(String id) {
        OAuthRequest request = oAuthService.createRequest(Verb.DELETE, LIVE_ENDPOINT + "/" + id);
        request.send();
    }

    /**
     * create or update the object's name and description (folder, album, file and photo)
     */
    public Map save(Map obj, String id, String parentId) {
        OAuthRequest request = null;
        if(StringUtil.isEmpty(id)){
            request = oAuthService.createRequest(Verb.POST, LIVE_ENDPOINT + "/" + parentId);
        }else{
            request = oAuthService.createRequest(Verb.PUT, LIVE_ENDPOINT + "/" + id);
        }
        request.addBodyParameter("name", (String) obj.get("name"));
        request.addBodyParameter("description", (String) obj.get("description"));
        Response response = request.send();
        Map info = JsonUtil.toMapAndList(response.getBody());
        return info;
    }

    /**
     * download by id
     */
    public InputStream download(String id) {
        OAuthRequest request = oAuthService.createRequest(Verb.GET, LIVE_ENDPOINT + "/" + id + "/content?download=true");
        Response response = request.send();
        return response.getStream();
    }

    public Map showPhotos(String folderId) {
        OAuthRequest request = oAuthService.createRequest(Verb.GET, LIVE_ENDPOINT + "/" + folderId + "/photos");
        Response response = request.send();
        Map profile = JsonUtil.toMapAndList(response.getBody());
        return profile;
    }

    public InputStream showPicture(String photoId) {
        OAuthRequest request = oAuthService.createRequest(Verb.GET, LIVE_ENDPOINT + "/" + photoId + "/picture");
        Response response = request.send();
        return response.getStream();
    }

}
