package com.britesnow.samplesocial.service;

import com.britesnow.samplesocial.dao.SocialIdEntityDao;
import com.britesnow.samplesocial.entity.SocialIdEntity;
import com.britesnow.samplesocial.oauth.OAuthServiceHelper;
import com.britesnow.samplesocial.oauth.OauthException;
import com.britesnow.samplesocial.oauth.OauthTokenExpireException;
import com.britesnow.samplesocial.oauth.ServiceType;
import com.britesnow.snow.util.JsonUtil;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.scribe.model.*;
import org.scribe.oauth.OAuthService;

import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.scribe.model.OAuthConstants.EMPTY_TOKEN;


@Singleton
public class FoursquareAuthService implements AuthService {

    @Inject
    private SocialIdEntityDao socialIdEntityDao;
    private OAuthService oAuthService;

    @Inject
    public FoursquareAuthService(OAuthServiceHelper oauthServiceHelper) {
        oAuthService = oauthServiceHelper.getOauthService(ServiceType.Foursquare);
    }

    @Override
    public SocialIdEntity getSocialIdEntity(Long userId) {
        SocialIdEntity socialId = socialIdEntityDao.getSocialdentity(userId, ServiceType.Foursquare);
        if (socialId != null) {
            if (socialId.getTokenDate().getTime() > System.currentTimeMillis()) {
                socialId.setValid(true);
            } else {
                throw new OauthTokenExpireException(getAuthorizationUrl());
            }
            return socialId;
        }
        //if result is null, need redo auth
        throw new OauthException(getAuthorizationUrl());
    }

    public String getAuthorizationUrl() {
        return oAuthService.getAuthorizationUrl(EMPTY_TOKEN);
    }

    public boolean updateAccessToken(String verifierCode, long userId) {
        Verifier verifier = new Verifier(verifierCode);
        Token accessToken = oAuthService.getAccessToken(EMPTY_TOKEN, verifier);
        if (accessToken.getToken() != null) {
            boolean newSocial = false;
            SocialIdEntity social = socialIdEntityDao.getSocialdentity(userId, ServiceType.Foursquare);
            if (social == null) {
                social = new SocialIdEntity();
                newSocial = true;
            }
            social.setUser_id(userId);
            social.setToken(accessToken.getToken());
            social.setService(ServiceType.Foursquare);
            if (newSocial) {
                socialIdEntityDao.save(social);
            } else {
                socialIdEntityDao.update(social);
            }
            return true;
        }
        throw new OauthException(getAuthorizationUrl());

    }
}
