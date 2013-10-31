package com.britesnow.samplesocial.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.scribe.model.OAuthConstants;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import com.britesnow.samplesocial.entity.SocialIdEntity;
import com.britesnow.samplesocial.manager.OAuthManager;
import com.britesnow.samplesocial.oauth.OAuthServiceHelper;
import com.britesnow.samplesocial.oauth.OauthException;
import com.britesnow.samplesocial.oauth.ServiceType;
import com.britesnow.snow.util.JsonUtil;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.inject.Inject;
import com.google.inject.Singleton;


@Singleton
public class LinkedInAuthService implements AuthService {

    private OAuthService oAuthService;
    @Inject
    private OAuthManager oAuthManager;
    @Inject
    private SocialService socialService;

    public static final String PROFILE_URL = "http://api.linkedin.com/v1/people/~:(email-address)";

    private final LoadingCache<String, Token> tokenCache;
    
    @Inject
    public LinkedInAuthService(OAuthServiceHelper oauthServiceHelper) {
        oAuthService = oauthServiceHelper.getOauthService(ServiceType.LinkedIn);
        tokenCache = CacheBuilder.newBuilder().maximumSize(10000).expireAfterAccess(3, TimeUnit.MINUTES)
                .build(new CacheLoader<String, Token>() {
                    @Override
                    public Token load(String oauthCode) throws Exception {
                        return OAuthConstants.EMPTY_TOKEN;
                    }
                });
    }

    /**
     * get linkedin socialid entity by user id.
     * @param userId   user id
     * @return linkedin socialid entity
     */
    public SocialIdEntity getSocialIdEntity() {
		SocialIdEntity socialId = socialService.getSocialIdEntityfromSession(ServiceType.LinkedIn);
        if(socialId == null){
        	//if result is null, need redo auth
        	throw new OauthException(getAuthorizationUrl());
        }
        return socialId;
    }

    /**
     * get authorization url and put in cache
     * @return url for authorization
     */
    public String getAuthorizationUrl() {
        Token reqToken = oAuthService.getRequestToken();
        tokenCache.put(reqToken.getToken(), reqToken);
        return oAuthService.getAuthorizationUrl(reqToken);
    }

    /**
     * update access token to database
     * @param requestToken requestToken
     * @param verifierCode verifierCode
     * @param userId  user id
     */
    public void updateAccessToken(String requestToken, String verifierCode, long userId)  {
        try {
            Verifier verifier = new Verifier(verifierCode);
            Token reqToken = tokenCache.get(requestToken);
            Token accessToken = oAuthService.getAccessToken(reqToken, verifier);
            if (accessToken.getToken() != null) {
                //get expire date

                OAuthRequest request = new OAuthRequest(Verb.GET, PROFILE_URL);
                request.addHeader("x-li-format","json");
                oAuthService.signRequest(accessToken, request);
                Response response = request.send();
                Map map = JsonUtil.toMapAndList(response.getBody());

                HashMap<String, String> managerMap = new HashMap<String, String>();
                managerMap.put("userId", userId+"");
                managerMap.put("access_token", accessToken.getToken());
                managerMap.put("secret", accessToken.getSecret());
                managerMap.put("email", (String) map.get("emailAddress"));
                oAuthManager.setInfo(ServiceType.LinkedIn, managerMap);
                
            }else{
                throw new OauthException(getAuthorizationUrl());
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
