package com.britesnow.samplesocial.web;


import java.util.Map;

import com.britesnow.samplesocial.entity.User;
import com.britesnow.samplesocial.service.TwitterService;
import com.britesnow.snow.web.RequestContext;
import com.britesnow.snow.web.param.annotation.WebUser;
import com.britesnow.snow.web.rest.annotation.WebGet;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class TwitterHandlers {
    @Inject
    private TwitterService twitterService;

    @WebGet("/twitter/getUserInfo")
    public WebResponse getContacts(@WebUser User user, RequestContext rc) throws Exception {
    	Map userInfo = twitterService.getUserInfo(user);
    	System.out.println(userInfo.get("id"));
    	WebResponse response = WebResponse.success(userInfo);
    	return response;
    }

}
