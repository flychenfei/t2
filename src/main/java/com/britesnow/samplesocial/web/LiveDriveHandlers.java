package com.britesnow.samplesocial.web;


import com.britesnow.samplesocial.entity.User;
import com.britesnow.samplesocial.service.LiveDriveService;
import com.britesnow.snow.util.JsonUtil;
import com.britesnow.snow.web.param.annotation.WebParam;
import com.britesnow.snow.web.param.annotation.WebUser;
import com.britesnow.snow.web.rest.annotation.WebGet;
import com.britesnow.snow.web.rest.annotation.WebPost;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.Map;

@Singleton
public class LiveDriveHandlers {
    @Inject
    private LiveDriveService liveDriveService;

    @WebGet("/liveDrive/getRootFolder")
    public WebResponse getRootFolder(@WebUser User user)  {
        if (user != null) {
            Map foldersList = liveDriveService.getRootFolder();
            return WebResponse.success(foldersList);
        }else {
            return WebResponse.fail();
        }
    }

    @WebGet("/liveDrive/getFolderFilesList")
    public WebResponse getFolderFilesList(@WebUser User user,@WebParam("id") String id)  {
        if (user != null) {
            Map filesList = liveDriveService.getFolderFilesList(id);
            return WebResponse.success(filesList);
        }else {
            return WebResponse.fail();
        }
    }

    @WebGet("/liveDrive/get")
    public WebResponse get(@WebUser User user,@WebParam("id") String id)  {
        Map objInfo = liveDriveService.get(id);
        return WebResponse.success(objInfo);
    }

    @WebPost("/liveDrive/delete")
    public WebResponse delete(@WebUser User user,@WebParam("id") String id)  {
        liveDriveService.delete(id);
        return WebResponse.success();
    }

    @WebPost("/liveDrive/save")
    public WebResponse save(@WebUser User user, @WebParam("id") String id, @WebParam("parentId") String parentId, @WebParam("objJson") String objJson)  {
        if (user != null) {
            Map obj = JsonUtil.toMapAndList(objJson);
            Map objInfo = liveDriveService.save(obj, id, parentId);
            return WebResponse.success(objInfo);
        }else {
            return WebResponse.fail();
        }
    }
    
    @WebGet("/liveDrive/showPhotos")
    public WebResponse showPhotos(@WebUser User user,@WebParam("id") String id)  {
        if (user != null) {
            Map photos = liveDriveService.showPhotos(id);
            return WebResponse.success(photos);
        }else {
            return WebResponse.fail();
        }
    }

}
