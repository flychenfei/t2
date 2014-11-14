package com.britesnow.samplesocial.web;


import java.util.Map;

import com.britesnow.samplesocial.entity.User;
import com.britesnow.samplesocial.service.LiveAblumService;
import com.britesnow.snow.util.JsonUtil;
import com.britesnow.snow.web.param.annotation.WebParam;
import com.britesnow.snow.web.param.annotation.WebUser;
import com.britesnow.snow.web.rest.annotation.WebGet;
import com.britesnow.snow.web.rest.annotation.WebPost;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class LiveAblumHandlers {
    @Inject
    private LiveAblumService liveAblumService;

    @WebGet("/liveAblum/getUserAblums")
    public WebResponse getUserAblums(@WebUser User user)  {
        if (user != null) {
            Map ablumsList = liveAblumService.getUserAblums();
            return WebResponse.success(ablumsList);
        }else {
            return WebResponse.fail();
        }
    }

    @WebGet("/liveAblum/getAblum")
    public WebResponse getAblum(@WebUser User user,@WebParam("id") String id)  {
        Map ablumInfo = liveAblumService.getAblum(id);
        return WebResponse.success(ablumInfo);
    }

    @WebPost("/liveAblum/saveAblum")
    public WebResponse saveAblum(@WebUser User user, @WebParam("id") String id, @WebParam("ablumJson") String ablumJson)  {
        if (user != null) {
            Map contact = JsonUtil.toMapAndList(ablumJson);
            Map contactInfo = liveAblumService.saveAblum(contact, id);
            return WebResponse.success(contactInfo);
        }else {
            return WebResponse.fail();
        }
    }

}
