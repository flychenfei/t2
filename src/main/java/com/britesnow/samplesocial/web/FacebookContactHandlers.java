package com.britesnow.samplesocial.web;

import java.util.List;
import java.util.Map;

import com.britesnow.samplesocial.entity.Contact;
import com.britesnow.samplesocial.entity.SocialIdEntity;
import com.britesnow.samplesocial.entity.User;
import com.britesnow.samplesocial.service.FContactService;
import com.britesnow.samplesocial.service.FacebookAuthService;
import com.britesnow.samplesocial.service.FacebookService;
import com.britesnow.snow.web.RequestContext;
import com.britesnow.snow.web.handler.annotation.WebActionHandler;
import com.britesnow.snow.web.handler.annotation.WebModelHandler;
import com.britesnow.snow.web.param.annotation.WebModel;
import com.britesnow.snow.web.param.annotation.WebParam;
import com.britesnow.snow.web.param.annotation.WebUser;
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

    @WebModelHandler(startsWith = "/fb/friends")
    public void getFacebookFriends(@WebModel Map m, @WebUser User user, @WebParam("pageSize") Integer pageSize,
                            @WebParam("pageIndex") Integer pageIndex, @WebParam("limit") Integer limit,
                            @WebParam("offset") Integer offset, RequestContext rc) {
        SocialIdEntity e = facebookAuthService.getSocialIdEntity(user.getId());
        String token = e.getToken();
        List ls = facebookService.getFriendsByPage(token, limit, offset);
        m.put("result", ls);
        if (ls != null && pageSize != null && ls.size() == pageSize) {
            m.put("hasNext", true);
        }
    }

    @WebModelHandler(startsWith = "/fb/contacts")
    public void getFacebookContacts(@WebModel Map m, @WebUser User user, @WebParam("pageSize") Integer pageSize,
                            @WebParam("pageIndex") Integer pageIndex, RequestContext rc) {
        List ls = fContactService.getContactsByPage(user);
        m.put("result", ls);
        if (ls != null && pageSize != null && ls.size() == pageSize) {
            m.put("hasNext", true);
        }
    }

    @WebModelHandler(startsWith = "/getFacebookFriendDetail")
    public void getFacebookFriendDetail(@WebModel Map m, @WebUser User user, @WebParam("fbid") String fbid,
                            RequestContext rc) {
        SocialIdEntity e = facebookAuthService.getSocialIdEntity(user.getId());
        String token = e.getToken();
        com.restfb.types.User friend = (com.restfb.types.User) facebookService.getFriendInformation(token, fbid);
        m.put("result", friend);
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

}
