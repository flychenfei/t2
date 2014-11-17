package com.britesnow.samplesocial.web;


import java.util.Map;

import com.britesnow.samplesocial.entity.User;
import com.britesnow.samplesocial.service.LiveContactService;
import com.britesnow.snow.util.JsonUtil;
import com.britesnow.snow.web.RequestContext;
import com.britesnow.snow.web.param.annotation.WebParam;
import com.britesnow.snow.web.param.annotation.WebUser;
import com.britesnow.snow.web.rest.annotation.WebGet;
import com.britesnow.snow.web.rest.annotation.WebPost;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class LiveContactHandlers {
    @Inject
    private LiveContactService liveContactService;

    @WebGet("/liveContact/getList")
    public WebResponse getList(@WebUser User user, RequestContext rc, @WebParam("isFriend") Boolean isFriend)  {
        if (user != null) {
            Map userInfo = liveContactService.listContact(isFriend);
            WebResponse response = WebResponse.success(userInfo);
            return response;
        }else {
            return WebResponse.fail();
        }
    }
    
    @WebPost("/liveContact/saveContact")
    public WebResponse saveContact(@WebUser User user,@WebParam("id") String id, @WebParam("contactJson") String contactJson)  {
        Map contact = JsonUtil.toMapAndList(contactJson);
        Map contactInfo = liveContactService.saveContact(contact, id);
        return WebResponse.success(contactInfo);
    }
    
    @WebGet("/liveContact/getContact")
    public WebResponse getContact(@WebUser User user,@WebParam("id") String id)  {
        Map contactInfo = liveContactService.getContact(id);
        return WebResponse.success(contactInfo);
    }
}
