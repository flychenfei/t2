package com.britesnow.samplesocial.web;


import java.util.Map;

import com.britesnow.samplesocial.entity.User;
import com.britesnow.samplesocial.service.LiveService;
import com.britesnow.snow.web.RequestContext;
import com.britesnow.snow.web.param.annotation.WebUser;
import com.britesnow.snow.web.rest.annotation.WebGet;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class LiveHandlers {
    @Inject
    private LiveService liveService;

    @WebGet("/live/getUserInfo")
    public WebResponse getUserInfo(@WebUser User user, RequestContext rc)  {
        if (user != null) {
            Map userInfo = liveService.getUserInfo(user.getId());
            WebResponse response = WebResponse.success(userInfo);
            return response;
        }else {
            return WebResponse.fail();
        }

    }

}
