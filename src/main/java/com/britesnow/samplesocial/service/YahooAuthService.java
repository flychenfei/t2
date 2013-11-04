package com.britesnow.samplesocial.service;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.scribe.model.OAuthConstants;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import com.britesnow.samplesocial.manager.OAuthManager;
import com.britesnow.samplesocial.model.SocialIdEntity;
import com.britesnow.samplesocial.oauth.OAuthServiceHelper;
import com.britesnow.samplesocial.oauth.OauthException;
import com.britesnow.samplesocial.oauth.ServiceType;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.inject.Inject;
import com.google.inject.Singleton;


@Singleton
public class YahooAuthService implements AuthService {

    private OAuthService oAuthService;
    @Inject
    private OAuthManager oAuthManager;

    public static final String PROFILE_URL = "http://api.linkedin.com/v1/people/~:(email-address)";

    private final LoadingCache<String, Token> tokenCache;

    @Inject
    public YahooAuthService(OAuthServiceHelper oauthServiceHelper) {
        oAuthService = oauthServiceHelper.getOauthService(ServiceType.Yahoo);
        tokenCache = CacheBuilder.newBuilder().maximumSize(10000).expireAfterAccess(3, TimeUnit.MINUTES)
                .build(new CacheLoader<String, Token>() {
                    @Override
                    public Token load(String oauthCode) throws Exception {
                        return OAuthConstants.EMPTY_TOKEN;
                    }
                });
    }

    public SocialIdEntity getSocialIdEntity() {
		SocialIdEntity socialId = oAuthManager.getSocialIdEntityfromSession(ServiceType.Yahoo);
        if(socialId == null){
        	//if result is null, need redo auth
        	throw new OauthException(getAuthorizationUrl());
        }
        return socialId;
    }

    public String getAuthorizationUrl() {
        Token reqToken = oAuthService.getRequestToken();
        tokenCache.put(reqToken.getToken(), reqToken);
        return oAuthService.getAuthorizationUrl(reqToken);
    }

    public boolean updateAccessToken(String requestToken, String verifierCode)  {
        try {
            Verifier verifier = new Verifier(verifierCode);
            Token reqToken = tokenCache.get(requestToken);
            Token accessToken = oAuthService.getAccessToken(reqToken, verifier);
            if (accessToken.getToken() != null) {
/*                OAuthRequest request = new OAuthRequest(Verb.GET, PROFILE_URL);
                request.addHeader("x-li-format","json");
                oAuthService.signRequest(accessToken, request);
                Response response = request.send();
                Map map = JsonUtil.toMapAndList(response.getBody());*/

                
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("email", null);
                map.put("access_token", accessToken.getToken());
                map.put("secret", accessToken.getSecret());
                oAuthManager.setInfo(ServiceType.Yahoo, map);
                return true;
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }
}
