package com.britesnow.samplesocial.web;


import java.util.Map;

import com.britesnow.samplesocial.service.GoogleCalendarService;
import com.britesnow.snow.web.param.annotation.WebModel;
import com.britesnow.snow.web.param.annotation.WebParam;
import com.britesnow.snow.web.rest.annotation.WebGet;
import com.britesnow.snow.web.rest.annotation.WebPost;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class GoogleCalendarHandlers {
    @Inject
    private GoogleCalendarService googleCalendarService;


    @WebGet("/googleCalendar/list")
    public Object listEvents(@WebModel Map m) throws Exception {
        return WebResponse.success(googleCalendarService.listEvents(0, 20)).set("result_count", 0);
    }
    
    @WebGet("/googleCalendar/get")
    public Object getEvents(@WebModel Map m, @WebParam("id") String id) throws Exception {
        return WebResponse.success(googleCalendarService.getEvent(id));
    }
    
    @WebPost("/googleCalendar/save")
    public Object saveEvents(@WebModel Map m, @WebParam("id") String id,  @WebParam("summary") String summary) throws Exception {
        
        if(id == null || "".equals(id)){
            googleCalendarService.createEvent(summary);
        }else{
            googleCalendarService.updateEvent(id, summary);
        }
        
        return WebResponse.success();
    }
    @WebPost("/googleCalendar/delete")
    public Object deleteEvents(@WebModel Map m, @WebParam("id") String id) throws Exception {
        googleCalendarService.deleteEvent(id);
        return WebResponse.success();
    }

}
