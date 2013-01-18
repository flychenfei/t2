package com.britesnow.samplesocial.web;

import com.britesnow.samplesocial.entity.User;
import com.britesnow.samplesocial.service.LinkedService;
import com.britesnow.snow.web.handler.annotation.WebModelHandler;
import com.britesnow.snow.web.param.annotation.WebModel;
import com.britesnow.snow.web.param.annotation.WebUser;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.Map;

@Singleton
public class LinkedInHandlers {
    @Inject
    private LinkedService linkedService;

    @WebModelHandler(startsWith = "/linkedin/connects")
    public void getConnects(@WebUser User user, @WebModel Map map) {
        Map result = linkedService.getConnections(user);
        map.put("result", result);
    }
}
