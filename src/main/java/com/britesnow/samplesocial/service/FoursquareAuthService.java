package com.britesnow.samplesocial.service;

import static org.scribe.model.OAuthConstants.EMPTY_TOKEN;

import java.util.HashMap;
import java.util.Map;

import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import com.britesnow.samplesocial.dao.SocialIdEntityDao;
import com.britesnow.samplesocial.entity.SocialIdEntity;
import com.britesnow.samplesocial.manager.OAuthManager;
import com.britesnow.samplesocial.oauth.OAuthServiceHelper;
import com.britesnow.samplesocial.oauth.OauthException;
import com.britesnow.samplesocial.oauth.ServiceType;
import com.britesnow.snow.web.binding.ApplicationProperties;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import fi.foyt.foursquare.api.FoursquareApi;


@Singleton
public class FoursquareAuthService implements AuthService {

    @Inject
    private SocialIdEntityDao socialIdEntityDao;
    private OAuthService oAuthService;
    private final CloneApi foursquareApi;
    private String secret,clienId;//callback;
    @Inject
    private OAuthManager oAuthManager;

    @Inject
    public FoursquareAuthService(OAuthServiceHelper oauthServiceHelper, @ApplicationProperties Map config) {
        oAuthService = oauthServiceHelper.getOauthService(ServiceType.Foursquare);
        clienId = (String) config.get("foursquare.client_id");
        secret = (String) config.get("foursquare.secret");
        String callback;
        callback = (String) config.get("foursquare.callback");
        foursquareApi = new CloneApi(clienId, secret, callback);
    }

    @Override
    public SocialIdEntity getSocialIdEntity(Long userId) {
        SocialIdEntity socialId = socialIdEntityDao.getSocialdentity(userId, ServiceType.Foursquare);
        if (socialId != null) {
            return socialId;
        }
        //if result is null, need redo auth
        throw new OauthException(getAuthorizationUrl());
    }

    public String getAuthorizationUrl() {
        return oAuthService.getAuthorizationUrl(EMPTY_TOKEN);
    }

    public void updateAccessToken(String verifierCode, long userId) {
        Verifier verifier = new Verifier(verifierCode);
        Token accessToken = oAuthService.getAccessToken(EMPTY_TOKEN, verifier);
        if (accessToken.getToken() != null) {
            foursquareApi.setoAuthToken(accessToken.getToken());
            boolean newSocial = false;
            SocialIdEntity social = socialIdEntityDao.getSocialdentity(userId, ServiceType.Foursquare);
            if (social == null) {
                social = new SocialIdEntity();
                newSocial = true;
            }
            social.setUser_id(userId);
            social.setToken(accessToken.getToken());
            social.setService(ServiceType.Foursquare);

            HashMap<String, String> managerMap = new HashMap<String, String>();
            managerMap.put("userId", userId+"");
            managerMap.put("access_token", accessToken.getToken());
            managerMap.put("secret", accessToken.getSecret());
            oAuthManager.setInfo(ServiceType.Foursquare, managerMap);
            
            if (newSocial) {
                socialIdEntityDao.save(social);
            } else {
                socialIdEntityDao.update(social);
            }
        }else{
            throw new OauthException(getAuthorizationUrl());
        }


    }

    public FoursquareApi getApi(Long userId) {
        SocialIdEntity soid = getSocialIdEntity(userId);
        FoursquareApi api = null;
        try {

            api = (FoursquareApi) foursquareApi.clone();
            if (soid != null) {
                api.setoAuthToken(soid.getToken());
            }
        } catch (CloneNotSupportedException e) {
            //imposible
            return null;
        }
        return api;
    }

    private class CloneApi extends FoursquareApi implements Cloneable {
        private CloneApi(String clientId, String clientSecret, String redirectUrl) {
            super(clientId, clientSecret, redirectUrl);
        }

        @Override
        public Object clone() throws CloneNotSupportedException {
            return super.clone();
        }
    }
}
