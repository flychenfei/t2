package com.britesnow.samplesocial.web;


import com.britesnow.samplesocial.entity.User;
import com.britesnow.samplesocial.service.LiveDriveService;
import com.britesnow.snow.util.JsonUtil;
import com.britesnow.snow.web.RequestContext;
import com.britesnow.snow.web.handler.annotation.WebResourceHandler;
import com.britesnow.snow.web.param.annotation.WebParam;
import com.britesnow.snow.web.param.annotation.WebUser;
import com.britesnow.snow.web.rest.annotation.WebGet;
import com.britesnow.snow.web.rest.annotation.WebPost;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

    @WebResourceHandler(matches = "/liveDrive/download")
    public void download(@WebUser User user, @WebParam("id") String id,@WebParam("fileName") String fileName, RequestContext rc)  throws IOException {
        InputStream in = liveDriveService.download(id);
        if (in != null) {
            HttpServletResponse res = rc.getRes();
            res.addHeader("Content-Disposition", "attachment;filename="+fileName);
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
    
    @WebGet("/liveDrive/showPhotos")
    public WebResponse showPhotos(@WebUser User user,@WebParam("id") String id)  {
        if (user != null) {
            Map photos = liveDriveService.showPhotos(id);
            return WebResponse.success(photos);
        }else {
            return WebResponse.fail();
        }
    }

    @WebResourceHandler(matches = "/liveDrive/showPicture")
    public void showPicture(@WebUser User user,@WebParam("id") String id, RequestContext rc) throws IOException {
        InputStream in = liveDriveService.showPicture(id);
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
