package com.britesnow.samplesocial.web;

import java.util.Map;

import com.britesnow.samplesocial.entity.User;
import com.britesnow.samplesocial.model.SocialIdEntity;
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
    private FacebookAuthService facebookAuthService;

    @WebGet("/fb/contacts")
    public Object getFacebookContacts(@WebModel Map<String, ?> m, @WebUser User user, @WebParam("query") String query,
                            @WebParam("pageSize") Integer pageSize, @WebParam("pageIndex") Integer pageIndex,
                            RequestContext rc) {
        m.put("result", null);
        return m;
    }

    @WebGet("/fb/friend-detail")
    public Object getFacebookFriendDetail(@WebModel Map<String, com.restfb.types.User> m, @WebUser User user, @WebParam("fbid") String fbid,
                            RequestContext rc) {
        SocialIdEntity e = facebookAuthService.getSocialIdEntity();
        String token = e.getToken();
        com.restfb.types.User friend = (com.restfb.types.User) facebookService.getUserInformation(token, fbid);
        m.put("result", friend);
        return m;
    }

    @WebPost("/fb/contact-add")
    public WebResponse addFacebookContact(@WebUser User user, @WebParam("groupId") Long groupId,
                            @WebParam("fbid") String fbid) {
        try {
            SocialIdEntity e = facebookAuthService.getSocialIdEntity();
            return WebResponse.success(e);
        } catch (Exception e) {
            e.printStackTrace();
            WebResponse.fail(e);
        }
        return null;
    }

    @WebPost("/fb/contact-delete")
    public WebResponse deleteFacebookContact(@WebParam("id") String id) {
        try {
            //fContactService.deleteContact(id);
            return WebResponse.success(true);
        } catch (Exception e) {
            e.printStackTrace();
            return WebResponse.fail(e);
        }
    }
}
