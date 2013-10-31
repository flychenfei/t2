package com.britesnow.samplesocial.service;

import java.util.HashMap;
import java.util.Map;

import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import com.britesnow.samplesocial.entity.SocialIdEntity;
import com.britesnow.samplesocial.manager.OAuthManager;
import com.britesnow.samplesocial.oauth.OAuthServiceHelper;
import com.britesnow.samplesocial.oauth.OauthException;
import com.britesnow.samplesocial.oauth.ServiceType;
import com.britesnow.snow.util.JsonUtil;
import com.google.inject.Inject;

public class SalesForceAuthService implements AuthService {

    private ServiceType       serivce     = ServiceType.SalesForce;
    private Token             EMPTY_TOKEN = null;
    private OAuthService      oAuthService;
    @Inject
    private SocialService socialService;
    
    @Inject
    private OAuthManager oAuthManager;

    @Inject
    public SalesForceAuthService(OAuthServiceHelper oauthServiceHelper) {
        oAuthService = oauthServiceHelper.getOauthService(ServiceType.SalesForce);
    }

    public SocialIdEntity getSocialIdEntity() {
		SocialIdEntity socialId = socialService.getSocialIdEntityfromSession(serivce);
        if(socialId == null){
        	//if result is null, need redo auth
        	throw new OauthException(getAuthorizationUrl());
        }
        return socialId;
    }
    
    
//    public SocialIdEntity getSocialIdEntityIgnoreAuth(Long userId){
//    	return socialIdEntityDao.getSocialdentity(userId, serivce);
//    }
    
    public String getAuthorizationUrl() {
        String authorizationUrl = oAuthService.getAuthorizationUrl(EMPTY_TOKEN);
        return authorizationUrl;
    }
    
    public String updateAccessToken(String code, Long userId) {
        Verifier verifier = new Verifier(code);
        Token accessToken = oAuthService.getAccessToken(EMPTY_TOKEN, verifier);
        
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("userId", userId+"");
        map.put("secret", null);
        map.put("email", null);
        map.put("access_token", accessToken.getToken());
        
        Map opts = JsonUtil.toMapAndList(accessToken.getRawResponse());
        map.putAll(opts);
        
        oAuthManager.setInfo(ServiceType.SalesForce, map);
        return accessToken.getToken();
    }

}
