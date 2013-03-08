package com.britesnow.samplesocial.service;

import com.britesnow.samplesocial.dao.SocialIdEntityDao;
import com.britesnow.samplesocial.entity.SocialIdEntity;
import com.britesnow.samplesocial.oauth.OAuthServiceHelper;
import com.britesnow.samplesocial.oauth.OauthException;
import com.britesnow.samplesocial.oauth.ServiceType;
import com.britesnow.snow.util.JsonUtil;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.scribe.model.OAuthConstants.EMPTY_TOKEN;


@Singleton
public class LiveAuthService implements AuthService {
    public static final String PROFILE_ENDPOINT = "https://apis.live.net/v5.0/me";
    @Inject
    private SocialIdEntityDao socialIdEntityDao;
    private OAuthService oAuthService;

    @Inject
    public LiveAuthService(OAuthServiceHelper oauthServiceHelper) {
        oAuthService = oauthServiceHelper.getOauthService(ServiceType.Live);
    }

    @Override
    public SocialIdEntity getSocialIdEntity(Long userId) {
        SocialIdEntity socialId = socialIdEntityDao.getSocialdentity(userId, ServiceType.Live);
        if (socialId != null && socialId.getTokenDate().getTime() - System.currentTimeMillis()  >0) {
            return socialId;
        }
        //if result is null, need redo auth
        throw new OauthException(getAuthorizationUrl());
    }

    public String getAuthorizationUrl() {
        return oAuthService.getAuthorizationUrl(EMPTY_TOKEN);
    }

    /**
     * update access token to database
     * @param verifierCode  verifier code that server return.
     * @param userId user id
     */
    public void updateAccessToken(String verifierCode, long userId) {
        Verifier verifier = new Verifier(verifierCode);
        Token accessToken = oAuthService.getAccessToken(EMPTY_TOKEN, verifier);
        if (accessToken.getToken() != null) {
            long expireDate = -1;
            String rawResponse = accessToken.getRawResponse();
            Pattern expire = Pattern.compile("\"expires_in\"\\s*:\\s*(\\d+)");
            Matcher matcher = expire.matcher(rawResponse);
            if (matcher.find()) {
                expireDate = System.currentTimeMillis() + (Integer.valueOf(matcher.group(1)) - 100) * 1000;
            }
            //get user profile
            //get userinfo
            OAuthRequest request = new OAuthRequest(Verb.GET, PROFILE_ENDPOINT);
            oAuthService.signRequest(accessToken, request);
            Response response = request.send();
            Map profile = JsonUtil.toMapAndList(response.getBody());
            String email = (String) ((Map) profile.get("emails")).get("account");
            SocialIdEntity social = socialIdEntityDao.getSocialdentity(userId, ServiceType.Live);
            boolean newSocial = false;
            if (social == null) {
                social = new SocialIdEntity();
                newSocial = true;
            }
            social.setUser_id(userId);
            social.setTokenDate(new Date(expireDate));
            social.setToken(accessToken.getToken());
            social.setService(ServiceType.Live);
            social.setEmail(email);
            if (newSocial) {
                socialIdEntityDao.save(social);
            } else {
                socialIdEntityDao.update(social);
            }
        }else{
            throw new OauthException(getAuthorizationUrl());
        }


    }

    /**
     * create request by user id, verb(get, post), and url
     * @param userId  user id
     * @param verb  http method, get, put, delelte post
     * @param url  url
     * @return  oauthrequest have sign
     */
    public OAuthRequest createRequest(Long userId, Verb verb, String url) {
        SocialIdEntity soid = getSocialIdEntity(userId);

        OAuthRequest request = new OAuthRequest(verb, url);
        oAuthService.signRequest(new Token(soid.getToken(), null), request);
        return request;
    }
}
