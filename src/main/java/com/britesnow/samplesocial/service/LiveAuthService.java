package com.britesnow.samplesocial.service;

import static org.scribe.model.OAuthConstants.EMPTY_TOKEN;

import java.util.HashMap;
import java.util.Map;

import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import com.britesnow.samplesocial.manager.OAuthManager;
import com.britesnow.samplesocial.model.SocialIdEntity;
import com.britesnow.samplesocial.oauth.OAuthServiceHelper;
import com.britesnow.samplesocial.oauth.OauthException;
import com.britesnow.samplesocial.oauth.ServiceType;
import com.britesnow.snow.util.JsonUtil;
import com.britesnow.snow.web.binding.ApplicationProperties;
import com.google.inject.Inject;
import com.google.inject.Singleton;


@Singleton
public class LiveAuthService implements AuthService {
    public static final String PROFILE_ENDPOINT = "https://apis.live.net/v5.0/me";
    private OAuthService oAuthService;
    private Map configMap;
    @Inject
    private OAuthManager oAuthManager;
    
    @Inject
    public LiveAuthService(OAuthServiceHelper oauthServiceHelper, @ApplicationProperties Map configMap) {
        oAuthService = oauthServiceHelper.getOauthService(ServiceType.Live);
        this.configMap = configMap;
    }

    public SocialIdEntity getSocialIdEntity() {
		SocialIdEntity socialId = oAuthManager.getSocialIdEntityfromSession(ServiceType.Live);
        if(socialId == null){
        	//if result is null, need redo auth
        	throw new OauthException(getAuthorizationUrl());
        }
        return socialId;
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
            //get user profile
            //get userinfo
            OAuthRequest request = new OAuthRequest(Verb.GET, PROFILE_ENDPOINT);
            oAuthService.signRequest(accessToken, request);
            Response response = request.send();
            Map profile = JsonUtil.toMapAndList(response.getBody());
            String email = (String) ((Map) profile.get("emails")).get("account");
            String prefix = "live";
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("userId", userId+"");
            map.put("access_token", accessToken.getToken());
            map.put("secret", configMap.get(prefix+".apiSecret").toString());
            map.put("email", email);
            oAuthManager.setInfo(ServiceType.Live, map);
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
    public OAuthRequest createRequest(Verb verb, String url) {
        SocialIdEntity soid = getSocialIdEntity();
        String secret = configMap.get("live.apiSecret").toString();
        OAuthRequest request = new OAuthRequest(verb, url);
        oAuthService.signRequest(new Token(soid.getToken(), secret), request);
        return request;
    }
}
