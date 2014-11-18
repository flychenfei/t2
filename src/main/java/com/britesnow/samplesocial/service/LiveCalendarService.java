package com.britesnow.samplesocial.service;

import com.britesnow.snow.util.JsonUtil;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Verb;

import java.util.Map;


@Singleton
public class LiveCalendarService {
    @Inject
	private LiveAuthService oAuthService;

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
}
