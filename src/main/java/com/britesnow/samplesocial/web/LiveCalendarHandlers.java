package com.britesnow.samplesocial.web;


import java.util.Map;

import com.britesnow.samplesocial.entity.User;
import com.britesnow.samplesocial.service.LiveCalendarService;
import com.britesnow.snow.web.param.annotation.WebParam;
import com.britesnow.snow.web.param.annotation.WebUser;
import com.britesnow.snow.web.rest.annotation.WebGet;
import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Singleton;

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
    
    @WebGet("/live/calendar/addUserCalendar")
    public WebResponse addUserCalendar(@WebUser User user, @WebParam("calendarName") String calendarName,
    	@WebParam("description") String description, @WebParam("location") String location )  {
        if (user != null && !Strings.isNullOrEmpty(calendarName)) {
            Map result = liveCalendarService.addUserCalendar(calendarName, description, location);
            return WebResponse.success(result);
        }else {
            return WebResponse.fail();
        }
    }
    
    @WebGet("/live/calendar/updateUserCalendar")
    public WebResponse updateUserCalendar(@WebUser User user, @WebParam("calendarId") String calendarId, @WebParam("calendarName") String calendarName,
    	@WebParam("description") String description, @WebParam("location") String location )  {
        if (user != null && !Strings.isNullOrEmpty(calendarName)) {
            Map result = liveCalendarService.updateUserCalendar(calendarId, calendarName, description, location);
            return WebResponse.success(result);
        }else {
            return WebResponse.fail();
        }
    }
    
    @WebGet("/live/calendar/deleteUserCalendar")
    public WebResponse deleteUserCalendar(@WebUser User user, @WebParam("calendarId") String calendarId)  {
        if (user != null && !Strings.isNullOrEmpty(calendarId)) {
            boolean result = liveCalendarService.deleteUserCalendar(calendarId);
            return WebResponse.success(result);
        }else {
            return WebResponse.fail();
        }
    }
}
