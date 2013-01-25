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

    public static final String CONNECTION_ENDPOINT = "http://api.linkedin.com/v1/people/~/connections:(id,first-name,last-name,industry)";
    public static final String JOB_ENDPOINT = "http://api.linkedin.com/v1/job-search?keywords=%s";
    public static final String COMPANY_ENDPOINT = "http://api.linkedin.com/v1/company-search?keywords=%s";

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

    public Map getConnections(User user, Integer pageIndex, Integer pageSize) {

        OAuthRequest request = createRequest(Verb.GET, CONNECTION_ENDPOINT);

        addPageParameter(pageIndex, pageSize, request);
        oAuthService.signRequest(getToken(user),request);
        Response response = request.send();
        Map map = JsonUtil.toMapAndList(response.getBody());
        return map;
    }

    private OAuthRequest createRequest(Verb verb, String url) {
        OAuthRequest request = new OAuthRequest(verb, url);
        request.addHeader("x-li-format","json");
        return request;
    }

    private void addPageParameter(Integer pageIndex, Integer pageSize, OAuthRequest request) {
        int start = pageIndex*pageSize;
        request.addQuerystringParameter("start", String.valueOf(start));
        request.addQuerystringParameter("count", String.valueOf(pageSize));
    }

    public Map searchJobs(User user, Integer pageIndex, Integer pageSize, String keywork) {
        if (keywork == null) {
            keywork = "hibernate";
        }
        OAuthRequest request = createRequest(Verb.GET, String.format(JOB_ENDPOINT, keywork));
        addPageParameter(pageIndex, pageSize, request);
        oAuthService.signRequest(getToken(user), request);
        Response resp = request.send();
        return JsonUtil.toMapAndList(resp.getBody());
    }
    public Map searchCompany(User user, Integer pageIndex, Integer pageSize, String keywork) {
        if (keywork == null) {
            keywork = "inc";
        }
        OAuthRequest request = createRequest(Verb.GET, String.format(COMPANY_ENDPOINT, keywork));
        addPageParameter(pageIndex, pageSize, request);
        oAuthService.signRequest(getToken(user), request);
        Response resp = request.send();
        return JsonUtil.toMapAndList(resp.getBody());
    }
}
