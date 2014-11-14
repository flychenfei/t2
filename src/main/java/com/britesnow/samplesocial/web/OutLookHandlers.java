package com.britesnow.samplesocial.web;


import com.britesnow.samplesocial.model.User;
import com.britesnow.snow.web.RequestContext;
import com.britesnow.snow.web.param.annotation.WebUser;
import com.britesnow.snow.web.rest.annotation.WebGet;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.Map;

import com.britesnow.samplesocial.service.OutLookService;

@Singleton
public class OutLookHandlers {
    @Inject
    private OutLookService outLookService;

    @WebGet("/live/OutLook/getUserInfo")
    public WebResponse getUserInfo(@WebUser User user)  {
        if (user != null) {
            Map userInfo = outLookService.getUserInfo();
            return WebResponse.success(userInfo);
        }else {
            return WebResponse.fail();
        }
    }

    @WebGet("/live/OutLook/getUserContactlist")
    public WebResponse getUserContacts(@WebUser User user)  {
        if (user != null) {
            Map contactList = outLookService.getUserContacts();
            return WebResponse.success(contactList);
        }else {
            return WebResponse.fail();
        }

    }
}
