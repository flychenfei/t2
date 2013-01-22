package com.britesnow.samplesocial.web;

import com.britesnow.samplesocial.entity.User;
import com.britesnow.samplesocial.service.LinkedService;
import com.britesnow.snow.web.param.annotation.WebUser;
import com.britesnow.snow.web.rest.annotation.WebGet;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.Map;

@Singleton
public class LinkedInHandlers {
    @Inject
    private LinkedService linkedService;

    @WebGet("/linkedin/connects")
    public Object getConnects(@WebUser User user) {
        Map result = linkedService.getConnections(user);
        return WebResponse.success(result);
    }
}
