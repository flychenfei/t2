package com.britesnow.samplesocial.service;

import java.util.Map;

import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Verb;

import com.britesnow.snow.util.JsonUtil;
import com.google.inject.Inject;
import com.google.inject.Singleton;


@Singleton
public class LiveService {
    @Inject
	private LiveAuthService oAuthService;

    /**
     * get user info
     * @param userId auth user id
     * @return user info map
     */
    public Map getUserInfo(Long userId) {
        OAuthRequest request = oAuthService.createRequest(userId, Verb.GET, LiveAuthService.PROFILE_ENDPOINT);
        Response response = request.send();
        Map profile = JsonUtil.toMapAndList(response.getBody());
        return profile;
    }




}
