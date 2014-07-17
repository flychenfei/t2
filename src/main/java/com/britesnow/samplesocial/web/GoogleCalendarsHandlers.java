package com.britesnow.samplesocial.web;


import java.util.List;
import java.util.Map;

import com.britesnow.samplesocial.service.GoogleCalendarsService;
import com.britesnow.snow.util.Pair;
import com.britesnow.snow.web.param.annotation.WebModel;
import com.britesnow.snow.web.param.annotation.WebParam;
import com.britesnow.snow.web.rest.annotation.WebGet;
import com.britesnow.snow.web.rest.annotation.WebPost;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class GoogleCalendarsHandlers {
    @Inject
    private GoogleCalendarsService googleCalendarsService;


    @WebGet("/googleCalendars/list")
    public Object CalendarList(@WebParam("pageSize") Integer pageSize) throws Exception {
        Pair<String, List<Map>> pair = googleCalendarsService.CalendarList(pageSize);
        List<Map> map = pair.getSecond();
        WebResponse result = WebResponse.success(map);
        result.set("nextPageToken", pair.getFirst());
        return result;
    }
    
    @WebGet("/googleCalendars/get")
    public Object getCalendar(@WebModel Map m, @WebParam("id") String id) throws Exception {
        return WebResponse.success(googleCalendarsService.getCalendar(id));
    }
    
    @WebPost("/googleCalendars/save")
    public Object saveCalendar(@WebParam("id") String id,@WebParam("summary") String summary) throws Exception {
        
        googleCalendarsService.saveCalendar(id, summary);
        return WebResponse.success();
    }
    @WebPost("/googleCalendars/delete")
    public Object deleteCalendar(@WebModel Map m, @WebParam("id") String id) throws Exception {
        googleCalendarsService.deleteCalendar(id);
        return WebResponse.success();
    }
    
    @WebPost("/googleShareCalendars/save")
    public Object saveShareCalendar(@WebParam("calendarId") String calendarId,@WebParam("role") String role,
                            @WebParam("scopeType") String scopeType,@WebParam("scopeValue") String scopeValue) throws Exception {
        
        googleCalendarsService.saveShareCalendar(calendarId, role, scopeType, scopeValue);
        return WebResponse.success();
    }
    
    @WebGet("/googleShareCalendars/get")
    public Object getShareCalendar(@WebParam("calendarId") String calendarId) throws Exception {
        Pair<String, List<Map>> pair = googleCalendarsService.getShareCalendar(calendarId);
        List<Map> map = pair.getSecond();
        WebResponse result = WebResponse.success(map);
        result.set("primaryId", pair.getFirst());
        return result;
    }
    
    @WebPost("/deleteSharedCalendar/delete")
    public Object deleteSharedCalendar(@WebParam("calendarId") String calendarId, @WebParam("ruleId") String ruleId) throws Exception {
        googleCalendarsService.deleteSharedCalendar(calendarId,ruleId);
        return WebResponse.success();
    }
    

}
