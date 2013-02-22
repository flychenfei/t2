package com.britesnow.samplesocial.web;

import java.util.List;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;

import com.britesnow.samplesocial.entity.SocialIdEntity;
import com.britesnow.samplesocial.entity.User;
import com.britesnow.samplesocial.service.FacebookAuthService;
import com.britesnow.samplesocial.service.FacebookService;
import com.britesnow.snow.web.RequestContext;
import com.britesnow.snow.web.param.annotation.WebModel;
import com.britesnow.snow.web.param.annotation.WebParam;
import com.britesnow.snow.web.param.annotation.WebUser;
import com.britesnow.snow.web.rest.annotation.WebGet;
import com.britesnow.snow.web.rest.annotation.WebPost;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class FacebookFeedHandlers {
    @Inject
    private FacebookService     facebookService;
    @Inject
    private FacebookAuthService facebookAuthService;

    @WebGet("/fb/posts")
    public Object getFacebookPosts(@WebModel Map m, @WebUser User user, @WebParam("query") String query,
                            @WebParam("pageSize") Integer pageSize, @WebParam("pageIndex") Integer pageIndex,
                            RequestContext rc) {
        SocialIdEntity e = facebookAuthService.getSocialIdEntity(user.getId());
        String token = e.getToken();
        List ls = facebookService.getObjectList(token, "feed", "me", null, null, pageSize, pageIndex);
        m.put("result", ls);
        if (ls != null && pageSize != null && ls.size() == pageSize) {
            m.put("hasNext", true);
        }
        return m;
    }

    @WebGet("/fb/photos")
    public Object getFacebookPhotos(@WebModel Map m, @WebUser User user, @WebParam("query") String query,
                            @WebParam("pageSize") Integer pageSize, @WebParam("pageIndex") Integer pageIndex,
                            RequestContext rc) {
        SocialIdEntity e = facebookAuthService.getSocialIdEntity(user.getId());
        String token = e.getToken();
        List ls = facebookService.getPhotoList(token, "me", pageSize, pageIndex);
        m.put("result", ls);
        if (ls != null && pageSize != null && ls.size() == pageSize) {
            m.put("hasNext", true);
        }
        return m;
    }

    @WebPost("/fb/post-add")
    public WebResponse addFacebookPost(@WebUser User user, @WebParam("value") String value) {
        try {
            SocialIdEntity e = facebookAuthService.getSocialIdEntity(user.getId());
            String token = e.getToken();
            facebookService.publishFeed(token, e.getFbid(), value);
            return WebResponse.success(true);
        } catch (Exception e) {
            e.printStackTrace();
            WebResponse.fail(e);
        }
        return null;
    }

    @WebPost("/fb/post-add-photo")
    public WebResponse addFacebookPhoto(@WebUser User user, @WebParam("fbid") String fbid,
                            @WebParam("data") String data, @WebParam("file") FileItem file) {
        try {
            SocialIdEntity e = facebookAuthService.getSocialIdEntity(user.getId());
            String token = e.getToken();
            facebookService.publishPhoto(token, "me", data, file.getInputStream());
            return WebResponse.success(true);
        } catch (Exception e) {
            e.printStackTrace();
            WebResponse.fail(e);
        }
        return null;
    }
}