package com.britesnow.samplesocial.web;


import java.util.List;
import java.util.Map;

import com.britesnow.samplesocial.service.GoogleCalendarSettingService;
import com.britesnow.snow.util.Pair;
import com.britesnow.snow.web.param.annotation.WebModel;
import com.britesnow.snow.web.param.annotation.WebParam;
import com.britesnow.snow.web.rest.annotation.WebGet;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class GoogleCalendarSettingHandlers {
    @Inject
    private GoogleCalendarSettingService googleCalendarSettingService;

    @WebGet("/googleCalendarSetting/list")
    public Object CalendarSetting(@WebParam("pageSize") Integer pageSize) throws Exception {
        Pair<String, List<Map>> pair = googleCalendarSettingService.CalendarSetting(pageSize);
        List<Map> map = pair.getSecond();
        WebResponse result = WebResponse.success(map);
        result.set("nextPageToken", pair.getFirst());
        return result;
    }
    @WebGet("/googleCalendarSetting/get")
    public Object getCalendarSetting(@WebModel Map m, @WebParam("settingId") String settingId) throws Exception {
        return WebResponse.success(googleCalendarSettingService.getCalendar(settingId));
    }

}
