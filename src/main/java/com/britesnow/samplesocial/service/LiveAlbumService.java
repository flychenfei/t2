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
public class LiveAlbumService {
    @Inject
	private LiveAuthService oAuthService;

    public static final String ALBUMLIST_ENDPOINT = "https://apis.live.net/v5.0/me/albums";
    public static final String LIVE_ENDPOINT = "https://apis.live.net/v5.0";

    /**
     * get user album lists
     * @return
     */
    public Map getUserAlbums() {
        OAuthRequest request = oAuthService.createRequest(Verb.GET, ALBUMLIST_ENDPOINT);
        Response response = request.send();
        Map profile = JsonUtil.toMapAndList(response.getBody());
        return profile;
    }

    public Map getAlbum(String albumId) {
        OAuthRequest request = oAuthService.createRequest(Verb.GET, LIVE_ENDPOINT + "/" + albumId);
        Response response = request.send();
        Map albumInfo = JsonUtil.toMapAndList(response.getBody());
        return albumInfo;
    }

    public void deleteAlbum(String albumId) {
        OAuthRequest request = oAuthService.createRequest(Verb.DELETE, LIVE_ENDPOINT + "/" + albumId);
        Response response = request.send();
    }

    /**
     * create album
     * @return
     */
    public Map saveAlbum(Map album, String albumId) {
        OAuthRequest request = null;
        if(StringUtil.isEmpty(albumId)){
            request = oAuthService.createRequest(Verb.POST, ALBUMLIST_ENDPOINT);
        }else{
            request = oAuthService.createRequest(Verb.PUT, LIVE_ENDPOINT + "/" + albumId);
        }
        request.addBodyParameter("name", (String) album.get("name"));
        request.addBodyParameter("description", (String) album.get("description"));
        Response response = request.send();
        Map albumInfo = JsonUtil.toMapAndList(response.getBody());
        return albumInfo;
    }

    public Map showPhotos(String albumId) {
        OAuthRequest request = oAuthService.createRequest(Verb.GET, LIVE_ENDPOINT + "/" + albumId + "/photos");
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
