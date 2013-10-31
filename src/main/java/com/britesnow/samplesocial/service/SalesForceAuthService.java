package com.britesnow.samplesocial.service;

import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import com.britesnow.samplesocial.entity.SocialIdEntity;
import com.britesnow.samplesocial.oauth.OAuthServiceHelper;
import com.britesnow.samplesocial.oauth.OauthException;
import com.britesnow.samplesocial.oauth.ServiceType;
import com.google.inject.Inject;

public class SalesForceAuthService implements AuthService {

    private ServiceType       serivce     = ServiceType.SalesForce;
    private Token             EMPTY_TOKEN = null;
    private OAuthService      oAuthService;
    @Inject
    private SocialService socialService;

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

    public String[] getAccessToken(String code) {
        Verifier verifier = new Verifier(code);
        Token accessToken = oAuthService.getAccessToken(EMPTY_TOKEN, verifier);
        return new String[] { accessToken.getToken(), accessToken.getSecret(), accessToken.getRawResponse() };
    }

}
