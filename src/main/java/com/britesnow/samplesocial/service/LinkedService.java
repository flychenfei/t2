package com.britesnow.samplesocial.service;

import com.britesnow.samplesocial.entity.SocialIdEntity;
import com.britesnow.samplesocial.entity.User;
import com.britesnow.samplesocial.oauth.OAuthType;
import com.britesnow.samplesocial.oauth.OAuthUtils;
import com.britesnow.samplesocial.oauth.OauthException;
import com.britesnow.snow.util.JsonUtil;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import java.util.Map;

@Singleton
public class LinkedService {
    @Inject
    private LinkedInAuthService authService;

    public static final String CONNECTION_URL = "http://api.linkedin.com/v1/people/~/connections:(id,first-name,last-name,industry)";

    private final OAuthService oAuthService;

    @Inject
    public LinkedService(OAuthUtils oAuthUtils) {
        oAuthService = oAuthUtils.getOauthService(OAuthType.LINKEDIN);
    }

    private Token getToken(User user) {
        SocialIdEntity soId = authService.getSocialIdEntity(user.getId());
        if (soId != null && soId.getTokenDate().getTime() > System.currentTimeMillis()) {
            return new Token(soId.getToken(), soId.getSecret());
        }
        throw new OauthException(oAuthService.getAuthorizationUrl(oAuthService.getRequestToken()));
    }

    public Map getConnections(User user) {

        OAuthRequest request = new OAuthRequest(Verb.GET, CONNECTION_URL);
        request.addHeader("x-li-format","json");
        oAuthService.signRequest(getToken(user),request);
        Response response = request.send();
        Map map = JsonUtil.toMapAndList(response.getBody());
        return map;
    }
}
