package com.britesnow.samplesocial.service;

import com.britesnow.snow.util.JsonUtil;
import com.google.gdata.util.common.base.StringUtil;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Verb;

import java.util.Map;


@Singleton
public class LiveFolderService {
    @Inject
	private LiveAuthService oAuthService;

    public static final String FOLDERLIST_ENDPOINT = "https://apis.live.net/v5.0/me/skydrive";
    public static final String LIVE_ENDPOINT = "https://apis.live.net/v5.0";

    /**
     * get user root folder (Skydrive)
     * @return
     */
    public Map getRootFolder() {
        OAuthRequest  request = oAuthService.createRequest(Verb.GET, FOLDERLIST_ENDPOINT);
        Response response = request.send();
        Map profile = JsonUtil.toMapAndList(response.getBody());
        return profile;
    }

    public Map getFolderFilesList(String folderId) {
        OAuthRequest request = oAuthService.createRequest(Verb.GET, LIVE_ENDPOINT + "/" + folderId + "/files");
        Response response = request.send();
        Map profile = JsonUtil.toMapAndList(response.getBody());
        return profile;
    }

    public Map getFolder(String folderId) {
        OAuthRequest request = oAuthService.createRequest(Verb.GET, LIVE_ENDPOINT + "/" + folderId);
        Response response = request.send();
        Map folderInfo = JsonUtil.toMapAndList(response.getBody());
        return folderInfo;
    }

    /**
     * create folder
     * @return
     */
    public Map saveFolder(Map folder, String folderId, String parentId) {
        OAuthRequest request = null;
        if(StringUtil.isEmpty(folderId)){
            request = oAuthService.createRequest(Verb.POST, LIVE_ENDPOINT + "/" + parentId);
        }else{
            request = oAuthService.createRequest(Verb.PUT, LIVE_ENDPOINT + "/" + folderId);
        }
        request.addBodyParameter("name", (String) folder.get("name"));
        request.addBodyParameter("description", (String) folder.get("description"));
        Response response = request.send();
        Map folderInfo = JsonUtil.toMapAndList(response.getBody());
        return folderInfo;
    }

}
