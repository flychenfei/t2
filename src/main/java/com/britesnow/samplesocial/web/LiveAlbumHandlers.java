package com.britesnow.samplesocial.web;


import java.util.Map;

import com.britesnow.samplesocial.entity.User;
import com.britesnow.samplesocial.service.LiveAlbumService;
import com.britesnow.snow.util.JsonUtil;
import com.britesnow.snow.web.param.annotation.WebParam;
import com.britesnow.snow.web.param.annotation.WebUser;
import com.britesnow.snow.web.rest.annotation.WebGet;
import com.britesnow.snow.web.rest.annotation.WebPost;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class LiveAlbumHandlers {
    @Inject
    private LiveAlbumService liveAlbumService;

    @WebGet("/liveAlbum/getUserAlbums")
    public WebResponse getUserAlbums(@WebUser User user)  {
        if (user != null) {
            Map albumsList = liveAlbumService.getUserAlbums();
            return WebResponse.success(albumsList);
        }else {
            return WebResponse.fail();
        }
    }

    @WebGet("/liveAlbum/getAlbum")
    public WebResponse getAlbum(@WebUser User user,@WebParam("id") String id)  {
        Map albumInfo = liveAlbumService.getAlbum(id);
        return WebResponse.success(albumInfo);
    }

    @WebPost("/liveAlbum/deleteAlbum")
    public WebResponse deletAlbum(@WebUser User user,@WebParam("id") String id)  {
        liveAlbumService.deleteAlbum(id);
        return WebResponse.success();
    }

    @WebPost("/liveAlbum/saveAlbum")
    public WebResponse saveAlbum(@WebUser User user, @WebParam("id") String id, @WebParam("albumJson") String albumJson)  {
        if (user != null) {
            Map album = JsonUtil.toMapAndList(albumJson);
            Map albumInfo = liveAlbumService.saveAlbum(album, id);
            return WebResponse.success(albumInfo);
        }else {
            return WebResponse.fail();
        }
    }

}
