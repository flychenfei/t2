package com.britesnow.samplesocial.web;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.britesnow.samplesocial.entity.User;
import com.britesnow.samplesocial.service.LiveAlbumService;
import com.britesnow.snow.util.JsonUtil;
import com.britesnow.snow.web.RequestContext;
import com.britesnow.snow.web.handler.annotation.WebResourceHandler;
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
    
    @WebGet("/liveAlbum/showPhotos")
    public WebResponse showPhotos(@WebUser User user,@WebParam("id") String id)  {
        if (user != null) {
            Map photos = liveAlbumService.showPhotos(id);
            return WebResponse.success(photos);
        }else {
            return WebResponse.fail();
        }
    }
    
    @WebResourceHandler(matches = "/liveAlbum/showPicture")
    public void showPicture(@WebUser User user,@WebParam("id") String id, RequestContext rc) throws IOException  {
        InputStream in = liveAlbumService.showPicture(id);
        if (in != null) {
            HttpServletResponse res = rc.getRes();
            OutputStream out = res.getOutputStream();
            res.setContentType("application/octet-stream");
            int length = 0;
            byte[] data = new byte[10240];
            while((length=in.read(data))!=-1){
                out.write(data, 0, length);
            }
            in.close();
            out.close(); 
        }
    }


}
