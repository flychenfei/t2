package com.britesnow.samplesocial.service;

import java.util.Map;

import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Verb;

import com.britesnow.snow.util.JsonUtil;
import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Singleton;


@Singleton
public class LiveCalendarService {
    @Inject
	private LiveAuthService oAuthService;

    public static final String LIVE_ENDPOINT = "https://apis.live.net/v5.0/";
    public static final String USER_CALENDARS_ENDPOINT = "https://apis.live.net/v5.0/me/calendars";

    /**
     * get user calendar lists
     * @return
     */
    public Map getUserCalendars() {
        OAuthRequest request = oAuthService.createRequest(Verb.GET, USER_CALENDARS_ENDPOINT);
        Response response = request.send();
        Map profile = JsonUtil.toMapAndList(response.getBody());
        return profile;
    }
    
    /**
     * add a calendar for user
     * @param calendarName
     * @param description
     * @param subscription_location
     * @return
     */
    public Map addUserCalendar(String calendarName, String description, String subscription_location) {
    	OAuthRequest request = oAuthService.createRequest(Verb.POST, USER_CALENDARS_ENDPOINT);
        request.addBodyParameter("name", calendarName);
        request.addBodyParameter("description", description);
        //request.addBodyParameter("subscription_location", subscription_location);
        Response response = request.send();
        Map profile = JsonUtil.toMapAndList(response.getBody());
        return profile;
    }
    
    /**
     * add a calendar for user
     * @param calendarName
     * @param description
     * @param subscription_location
     * @return
     */
    public Map updateUserCalendar(String calendarId, String calendarName, String description, String subscription_location) {
    	OAuthRequest request = oAuthService.createRequest(Verb.PUT, LIVE_ENDPOINT+calendarId);
        request.addBodyParameter("name", calendarName);
        request.addBodyParameter("description", description);
        //request.addBodyParameter("subscription_location", subscription_location);
        Response response = request.send();
        Map profile = JsonUtil.toMapAndList(response.getBody());
        return profile;
    }
    
    /**
     * delete user calendar
     * @param calendarId
     * @return
     */
    public boolean deleteUserCalendar(String calendarId) {
        OAuthRequest request = oAuthService.createRequest(Verb.DELETE, LIVE_ENDPOINT+calendarId);
        Response response = request.send();
        response.getBody();
        return Strings.isNullOrEmpty(response.getBody());
    }
}
