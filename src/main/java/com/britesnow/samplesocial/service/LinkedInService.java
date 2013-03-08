package com.britesnow.samplesocial.service;

import java.util.Map;

import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import com.britesnow.samplesocial.entity.SocialIdEntity;
import com.britesnow.samplesocial.entity.User;
import com.britesnow.samplesocial.oauth.OAuthServiceHelper;
import com.britesnow.samplesocial.oauth.OauthException;
import com.britesnow.samplesocial.oauth.ServiceType;
import com.britesnow.snow.util.JsonUtil;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class LinkedInService {
    @Inject
    private LinkedInAuthService authService;

    public static final String CONNECTION_ENDPOINT = "http://api.linkedin.com/v1/people/~/connections:(id,first-name,last-name,industry)";
    public static final String JOB_ENDPOINT = "http://api.linkedin.com/v1/job-search?keywords=%s";
    public static final String COMPANY_ENDPOINT = "http://api.linkedin.com/v1/company-search?keywords=%s";
    public static final String PEOPLE_SEARCH_ENDPOINT = "http://api.linkedin.com/v1/people-search?keywords=%s";

    private OAuthService oAuthService;

    @Inject
    public LinkedInService(OAuthServiceHelper oauthServiceFactory) {
        oAuthService = oauthServiceFactory.getOauthService(ServiceType.LinkedIn);
    }

    private Token getToken(User user) {
        SocialIdEntity soId = authService.getSocialIdEntity(user.getId());
        if (soId != null && soId.getTokenDate().getTime() > System.currentTimeMillis()) {
            return new Token(soId.getToken(), soId.getSecret());
        }
        throw new OauthException(oAuthService.getAuthorizationUrl(oAuthService.getRequestToken()));
    }

    /**
     * get user connections   by auth user
     * @param user  login user
     * @param pageIndex  page index
     * @param pageSize   page size
     * @return  user map
     */
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

    /**
     * search jobs by auth user
     * @param user   user have auth
     * @param pageIndex  page index
     * @param pageSize   page size
     * @param keywork    keywork to search
     * @return  job map
     */
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

    /**
     * search compan by auth user
     * @param user   user have auth
     * @param pageIndex  page index
     * @param pageSize   page size
     * @param keywork    key work to search
     * @return   company map
     */
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

    /**
     * search people  by auth user
     * @param user   auth user
     * @param pageIndex  page index
     * @param pageSize   page side
     * @param keywork   keywork to search
     * @return   people map
     */
    public Map searchPeople(User user, Integer pageIndex, Integer pageSize, String keywork) {
        if (keywork == null) {
            keywork = "self";
        }
        OAuthRequest request = createRequest(Verb.GET, String.format(PEOPLE_SEARCH_ENDPOINT, keywork));
        addPageParameter(pageIndex, pageSize, request);
        oAuthService.signRequest(getToken(user), request);
        Response resp = request.send();
        return JsonUtil.toMapAndList(resp.getBody());
    }

}
