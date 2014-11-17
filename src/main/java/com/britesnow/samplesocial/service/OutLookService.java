package com.britesnow.samplesocial.service;

import com.britesnow.snow.util.JsonUtil;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Verb;

import java.util.Map;


@Singleton
public class OutLookService {
    @Inject
	private LiveAuthService oAuthService;

    public static final String PROFILE_ENDPOINT = "https://apis.live.net/v5.0/me";
    public static final String CONTACTLIST_ENDPOINT = "https://apis.live.net/v5.0/me/contacts";
    public static final String ALBUMLIST_ENDPOINT = "https://apis.live.net/v5.0/me/albums";
    /**
     * get user info
     * @return user info map
     */
    public Map getUserInfo() {
        OAuthRequest request = oAuthService.createRequest(Verb.GET, PROFILE_ENDPOINT);
        Response response = request.send();
        Map profile = JsonUtil.toMapAndList(response.getBody());
        return profile;
    }

    /**
     * get user contact lists
     * @return
     */
    public Map getUserContacts() {
        OAuthRequest request = oAuthService.createRequest(Verb.GET, CONTACTLIST_ENDPOINT);
        Response response = request.send();
        Map profile = JsonUtil.toMapAndList(response.getBody());
        return profile;
    }

}
