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
        List ls = facebookService.getObjectList(token, "me", "feed", null, null, pageSize, pageIndex);
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

    @WebGet("/fb/notes")
    public Object getFacebookNotes(@WebModel Map m, @WebUser User user, @WebParam("query") String query,
                            @WebParam("pageSize") Integer pageSize, @WebParam("pageIndex") Integer pageIndex,
                            RequestContext rc) {
        SocialIdEntity e = facebookAuthService.getSocialIdEntity(user.getId());
        String token = e.getToken();
        List ls = facebookService.getNotesList(token, "me", pageSize, pageIndex);
        m.put("result", ls);
        if (ls != null && pageSize != null && ls.size() == pageSize) {
            m.put("hasNext", true);
        }
        return m;
    }

    @WebGet("/fb/events")
    public Object getFacebookEvents(@WebModel Map m, @WebUser User user, @WebParam("query") String query,
                            @WebParam("pageSize") Integer pageSize, @WebParam("pageIndex") Integer pageIndex,
                            RequestContext rc) {
        SocialIdEntity e = facebookAuthService.getSocialIdEntity(user.getId());
        String token = e.getToken();
        List ls = facebookService.getEventsList(token, "me", pageSize, pageIndex);
        m.put("result", ls);
        if (ls != null && pageSize != null && ls.size() == pageSize) {
            m.put("hasNext", true);
        }
        return m;
    }

    @WebGet("/fb/question")
    public Object getFacebookQuestions(@WebModel Map m, @WebUser User user, @WebParam("query") String query,
                            @WebParam("pageSize") Integer pageSize, @WebParam("pageIndex") Integer pageIndex,
                            RequestContext rc) {
        SocialIdEntity e = facebookAuthService.getSocialIdEntity(user.getId());
        String token = e.getToken();
        List ls = facebookService.getQuestionsList(token, "me", pageSize, pageIndex);
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

    @WebPost("/fb/post-delete")
    public WebResponse deleteFacebookPost(@WebUser User user, @WebParam("messageId") String messageId) {
        try {
            SocialIdEntity e = facebookAuthService.getSocialIdEntity(user.getId());
            String token = e.getToken();
            facebookService.deleteFeed(token, messageId);
            return WebResponse.success(true);
        } catch (Exception e) {
            e.printStackTrace();
            WebResponse.fail(e);
        }
        return null;
    }

    @WebPost("/fb/note-add")
    public WebResponse addFacebookNote(@WebUser User user, @WebParam("subject") String subject,
                            @WebParam("msg") String msg) {
        try {
            SocialIdEntity e = facebookAuthService.getSocialIdEntity(user.getId());
            String token = e.getToken();
            facebookService.publishNote(token, "me", subject, msg);
            return WebResponse.success(true);
        } catch (Exception e) {
            e.printStackTrace();
            WebResponse.fail(e);
        }
        return null;
    }

    @WebPost("/fb/note-delete")
    public WebResponse deleteFacebookNote(@WebUser User user, @WebParam("messageId") String messageId) {
        try {
            SocialIdEntity e = facebookAuthService.getSocialIdEntity(user.getId());
            String token = e.getToken();
            facebookService.deleteNote(token, messageId);
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

    @WebPost("/fb/delete-photo")
    public WebResponse deleteFacebookPhoto(@WebUser User user, @WebParam("messageId") String messageId) {
        try {
            SocialIdEntity e = facebookAuthService.getSocialIdEntity(user.getId());
            String token = e.getToken();
            facebookService.deletePhoto(token, messageId);
            return WebResponse.success(true);
        } catch (Exception e) {
            e.printStackTrace();
            WebResponse.fail(e);
        }
        return null;
    }

    @WebPost("/fb/event-add")
    public WebResponse addFacebookEvent(@WebUser User user, @WebParam("name") String name,
                            @WebParam("start_time") String start_time) {
        try {
            SocialIdEntity e = facebookAuthService.getSocialIdEntity(user.getId());
            String token = e.getToken();
            facebookService.publishEvent(token, "me", name, start_time);
            return WebResponse.success(true);
        } catch (Exception e) {
            e.printStackTrace();
            WebResponse.fail(e);
        }
        return null;
    }

    @WebPost("/fb/delete-event")
    public WebResponse deleteFacebookEvent(@WebUser User user, @WebParam("messageId") String messageId) {
        try {
            SocialIdEntity e = facebookAuthService.getSocialIdEntity(user.getId());
            String token = e.getToken();
            facebookService.deleteEvent(token, messageId);
            return WebResponse.success(true);
        } catch (Exception e) {
            e.printStackTrace();
            WebResponse.fail(e);
        }
        return null;
    }
}