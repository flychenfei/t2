package com.britesnow.samplesocial.web;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.britesnow.samplesocial.entity.Contact;
import com.britesnow.samplesocial.entity.SocialIdEntity;
import com.britesnow.samplesocial.entity.User;
import com.britesnow.samplesocial.service.FContactService;
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
public class FacebookContactHandlers {
    @Inject
    private FacebookService     facebookService;
    @Inject
    private FContactService     fContactService;

    @Inject
    private FacebookAuthService facebookAuthService;

    @WebGet("/fb/friends")
    public Object getFacebookFriends(@WebModel Map m, @WebUser User user, @WebParam("query") String query,
                            @WebParam("pageSize") Integer pageSize, @WebParam("pageIndex") Integer pageIndex,
                            @WebParam("limit") Integer limit, @WebParam("offset") Integer offset, RequestContext rc) {
        SocialIdEntity e = facebookAuthService.getSocialIdEntity(user.getId());
        String token = e.getToken();
        List ls = facebookService.getFriendsByPage(token, query, limit, offset);
        List ls2 = fContactService.getContactsByPage(user, null);
        Set filterSet = new HashSet();
        for (int i = 0; i < ls2.size(); i++) {
            Contact c = (Contact) ls2.get(i);
            filterSet.add(c.getFbid());
        }
        List ls3 = new ArrayList();
        for (int i = 0; i < ls.size(); i++) {
            com.restfb.types.User u = (com.restfb.types.User) ls.get(i);
            if (!filterSet.contains(u.getId().toString())) {
                ls3.add(u);
            }
        }
        m.put("result", ls3);
        if (ls3 != null && pageSize != null && ls3.size() == pageSize) {
            m.put("hasNext", true);
        }
        return m;
    }

    @WebGet("/fb/contacts")
    public Object getFacebookContacts(@WebModel Map m, @WebUser User user, @WebParam("query") String query,
                            @WebParam("pageSize") Integer pageSize, @WebParam("pageIndex") Integer pageIndex,
                            RequestContext rc) {
        List ls = fContactService.getContactsByPage(user, query);
        m.put("result", ls);
        if (ls != null && pageSize != null && ls.size() == pageSize) {
            m.put("hasNext", true);
        }
        return m;
    }

    @WebGet("/fb/friend-detail")
    public Object getFacebookFriendDetail(@WebModel Map m, @WebUser User user, @WebParam("fbid") String fbid,
                            RequestContext rc) {
        SocialIdEntity e = facebookAuthService.getSocialIdEntity(user.getId());
        String token = e.getToken();
        com.restfb.types.User friend = (com.restfb.types.User) facebookService.getFriendInformation(token, fbid);
        m.put("result", friend);
        return m;
    }

    @WebPost("/fb/contact-add")
    public WebResponse addFacebookContact(@WebUser User user, @WebParam("groupId") Long groupId,
                            @WebParam("fbid") String fbid) {
        try {
            SocialIdEntity e = facebookAuthService.getSocialIdEntity(user.getId());
            String token = e.getToken();
            Contact c = fContactService.addContact(token, groupId, fbid);
            return WebResponse.success(c);
        } catch (Exception e) {
            e.printStackTrace();
            WebResponse.fail(e);
        }
        return null;
    }

    @WebPost("/fb/contact-delete")
    public WebResponse deleteFacebookContact(@WebParam("id") String id) {
        try {
            fContactService.deleteContact(id);
            return WebResponse.success(true);
        } catch (Exception e) {
            e.printStackTrace();
            return WebResponse.fail(e);
        }
    }

    @WebGet("/fb/posts")
    public Object getFacebookPosts(@WebModel Map m, @WebUser User user, @WebParam("query") String query,
                            @WebParam("pageSize") Integer pageSize, @WebParam("pageIndex") Integer pageIndex,
                            RequestContext rc) {
        SocialIdEntity e = facebookAuthService.getSocialIdEntity(user.getId());
        String token = e.getToken();
        List ls = facebookService.getFeedList(token, null);
        m.put("result", ls);
        if (ls != null && pageSize != null && ls.size() == pageSize) {
            m.put("hasNext", true);
        }
        return m;
    }
}
