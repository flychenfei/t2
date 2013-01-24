package com.britesnow.samplesocial.service;

import java.util.Map;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import com.britesnow.samplesocial.dao.SocialIdEntityDao;
import com.britesnow.samplesocial.entity.Service;
import com.britesnow.samplesocial.entity.SocialIdEntity;
import com.britesnow.samplesocial.oauth.api.SalesForceApi;
import com.britesnow.snow.web.binding.ApplicationProperties;
import com.google.inject.Inject;

public class SalesForceAuthService implements AuthService {
    @Inject
    @ApplicationProperties
    private Map               cfg;

    @Inject
    private SocialIdEntityDao socialIdEntityDao;

    private Service           serivce     = Service.SalesForce;
    private Token             EMPTY_TOKEN = null;

    @Override
    public SocialIdEntity getSocialIdEntity(Long userId) {
        return socialIdEntityDao.getSocialdentity(userId, serivce);
    }

    public String getAuthorizationUrl() {
        OAuthService service = new ServiceBuilder().provider(SalesForceApi.class).apiKey(getApiKey()).apiSecret(getApiSecret()).callback(getCallBackUrl()).build();
        String authorizationUrl = service.getAuthorizationUrl(EMPTY_TOKEN);
        return authorizationUrl;
    }

    public String[] getAccessToken(String code) {
        OAuthService service = new ServiceBuilder().provider(SalesForceApi.class).apiKey(getApiKey()).apiSecret(getApiSecret()).callback(getCallBackUrl()).build();
        Verifier verifier = new Verifier(code);
        Token accessToken = service.getAccessToken(EMPTY_TOKEN, verifier);
        return new String[] { accessToken.getToken(), accessToken.getSecret(), accessToken.getRawResponse() };
    }

    public String getApiKey() {
        return (String) cfg.get("salesforce.apiKey");
    }

    public String getApiSecret() {
        return (String) cfg.get("salesforce.apiSecret");
    }

    public String getCallBackUrl() {
        return (String) cfg.get("salesforce.callbackUrl");
    }

}
