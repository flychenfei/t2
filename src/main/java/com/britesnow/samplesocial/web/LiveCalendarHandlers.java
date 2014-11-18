package com.britesnow.samplesocial.web;


import com.britesnow.samplesocial.entity.User;
import com.britesnow.samplesocial.service.LiveCalendarService;
import com.britesnow.snow.util.JsonUtil;
import com.britesnow.snow.web.param.annotation.WebParam;
import com.britesnow.snow.web.param.annotation.WebUser;
import com.britesnow.snow.web.rest.annotation.WebGet;
import com.britesnow.snow.web.rest.annotation.WebPost;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.Map;

@Singleton
public class LiveCalendarHandlers {
    @Inject
    private LiveCalendarService liveCalendarService;

    @WebGet("/live/calendar/getUserCalendars")
    public WebResponse getUserCalendars(@WebUser User user)  {
        if (user != null) {
            Map contactList = liveCalendarService.getUserCalendars();
            return WebResponse.success(contactList);
        }else {
            return WebResponse.fail();
        }
    }
}
