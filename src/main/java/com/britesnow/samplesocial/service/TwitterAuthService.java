package com.britesnow.samplesocial.service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.scribe.model.OAuthConstants;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import com.britesnow.samplesocial.dao.SocialIdEntityDao;
import com.britesnow.samplesocial.entity.SocialIdEntity;
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
	
	@Inject
    private SocialIdEntityDao socialIdEntityDao;
    private OAuthService oAuthService;
    
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
    
    public SocialIdEntity getSocialIdEntity(Long userId) {
        SocialIdEntity socialId = socialIdEntityDao.getSocialdentity(userId, ServiceType.Twitter);
        if (socialId != null) {
            socialId.setValid(true);
        }
        return socialId;
    }
    
    public void updateAccessToken(String requestTok, String verifierCode, Long id) throws Exception {
    	Token requestToken = tokenCache.get(requestTok);
    	Verifier verifier = new Verifier(verifierCode);
    	Token accessToken = oAuthService.getAccessToken(requestToken, verifier);
        if (accessToken.getToken() != null) {
            SocialIdEntity social = socialIdEntityDao.getSocialdentity(id, ServiceType.Twitter);
            boolean newSocial = false;
            if (social == null) {
                social = new SocialIdEntity();
                newSocial = true;
            }
            
            social.setUser_id(id);
            social.setToken(accessToken.getToken());
            social.setSecret(accessToken.getSecret());
            social.setService(ServiceType.Twitter);
            if (newSocial) {
                socialIdEntityDao.save(social);
            } else {
                socialIdEntityDao.update(social);
            }
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