package com.britesnow.samplesocial.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.scribe.model.OAuthConstants;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import com.britesnow.samplesocial.manager.OAuthManager;
import com.britesnow.samplesocial.model.SocialIdEntity;
import com.britesnow.samplesocial.oauth.OAuthServiceHelper;
import com.britesnow.samplesocial.oauth.OauthException;
import com.britesnow.samplesocial.oauth.ServiceType;
import com.britesnow.snow.web.binding.ApplicationProperties;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.inject.Singleton;

@Singleton
public class TwitterAuthService implements AuthService{
	@Inject
    @ApplicationProperties
    private Map cfg;
	
    private OAuthService oAuthService;
    @Inject
    private OAuthManager oAuthManager;
    
    private final LoadingCache<String, Token> tokenCache;

    @Inject
    public TwitterAuthService(OAuthServiceHelper oauthServiceHelper) {
        oAuthService = oauthServiceHelper.getOauthService(ServiceType.Twitter);
        tokenCache = CacheBuilder.newBuilder().maximumSize(10000).expireAfterAccess(3, TimeUnit.MINUTES)
                .build(new CacheLoader<String, Token>() {
                    @Override
                    public Token load(String oauthCode) throws Exception {
                        return OAuthConstants.EMPTY_TOKEN;
                    }
                });
    }

    public String getAuthorizationUrl() {
		Token requestToken = oAuthService.getRequestToken();
		tokenCache.put(requestToken.getToken(), requestToken);
		String url = oAuthService.getAuthorizationUrl(requestToken);
		return url;
    	
    }
    
    public SocialIdEntity getSocialIdEntity() {
		SocialIdEntity socialId = oAuthManager.getSocialIdEntityfromSession(ServiceType.Twitter);
        if(socialId == null){
        	//if result is null, need redo auth
        	throw new OauthException(getAuthorizationUrl());
        }
        return socialId;
    }
    
    public void updateAccessToken(String requestTok, String verifierCode) throws Exception {
    	Token requestToken = tokenCache.get(requestTok);
    	Verifier verifier = new Verifier(verifierCode);
    	Token accessToken = oAuthService.getAccessToken(requestToken, verifier);
        if (accessToken.getToken() != null) {
            
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("email", null);
            map.put("access_token", accessToken.getToken());
            map.put("secret", accessToken.getSecret());
            oAuthManager.setInfo(ServiceType.Twitter, map);
            
        } else {
        	throw new OauthException(getAuthorizationUrl());
        }
    	
	}

    public String getApiKey() {
        return (String) cfg.get("twitter.apiKey");
    }
    
    public String getApiSecret() {
        return (String) cfg.get("twitter.apiSecret");
    }
    
    public String getCallBackUrl() {
        return (String) cfg.get("twitter.callBackUrl");
    }

	

}