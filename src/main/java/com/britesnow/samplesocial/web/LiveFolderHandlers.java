package com.britesnow.samplesocial.web;


import com.britesnow.samplesocial.entity.User;
import com.britesnow.samplesocial.service.LiveFolderService;
import com.britesnow.snow.util.JsonUtil;
import com.britesnow.snow.web.param.annotation.WebParam;
import com.britesnow.snow.web.param.annotation.WebUser;
import com.britesnow.snow.web.rest.annotation.WebGet;
import com.britesnow.snow.web.rest.annotation.WebPost;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.Map;

@Singleton
public class LiveFolderHandlers {
    @Inject
    private LiveFolderService liveFolderService;

    @WebGet("/liveFolder/getRootFolder")
    public WebResponse getRootFolder(@WebUser User user)  {
        if (user != null) {
            Map foldersList = liveFolderService.getRootFolder();
            return WebResponse.success(foldersList);
        }else {
            return WebResponse.fail();
        }
    }

    @WebGet("/liveFolder/getFolderFilesList")
    public WebResponse getFolderFilesList(@WebUser User user,@WebParam("id") String id)  {
        if (user != null) {
            Map filesList = liveFolderService.getFolderFilesList(id);
            return WebResponse.success(filesList);
        }else {
            return WebResponse.fail();
        }
    }

    @WebGet("/liveFolder/getFolder")
    public WebResponse geFolder(@WebUser User user,@WebParam("id") String id)  {
        Map folderInfo = liveFolderService.getFolder(id);
        return WebResponse.success(folderInfo);
    }

    @WebPost("/liveFolder/deleteFolder")
    public WebResponse deleteFolder(@WebUser User user,@WebParam("id") String id)  {
        liveFolderService.deleteFolder(id);
        return WebResponse.success();
    }

    @WebPost("/liveFolder/saveFolder")
    public WebResponse saveFolder(@WebUser User user, @WebParam("id") String id, @WebParam("parentId") String parentId, @WebParam("folderJson") String folderJson)  {
        if (user != null) {
            Map folder = JsonUtil.toMapAndList(folderJson);
            Map folderInfo = liveFolderService.saveFolder(folder, id, parentId);
            return WebResponse.success(folderInfo);
        }else {
            return WebResponse.fail();
        }
    }

}
