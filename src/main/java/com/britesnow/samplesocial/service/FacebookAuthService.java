package com.britesnow.samplesocial.service;

import java.util.Map;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.FacebookApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import com.britesnow.samplesocial.dao.SocialIdEntityDao;
import com.britesnow.samplesocial.entity.Service;
import com.britesnow.samplesocial.entity.SocialIdEntity;
import com.britesnow.snow.web.binding.ApplicationProperties;
import com.google.inject.Inject;

public class FacebookAuthService implements AuthService {
    @Inject
    @ApplicationProperties
    private Map               cfg;

    @Inject
    private SocialIdEntityDao socialIdEntityDao;

    private Service           serivce     = Service.FaceBook;
    private Token             EMPTY_TOKEN = null;

    @Override
    public SocialIdEntity getSocialIdEntity(Long userId) {
        return socialIdEntityDao.getSocialdentity(userId, serivce);
    }

    public String getAuthorizationUrl() {
        OAuthService service = new ServiceBuilder().provider(FacebookApi.class).apiKey(getApiKey()).apiSecret(getApiSecret()).scope("publish_actions").scope("stream_publish").scope("create_event").callback(getCallBackUrl()).build();
        String authorizationUrl = service.getAuthorizationUrl(EMPTY_TOKEN);
        System.out.println(authorizationUrl);
        return authorizationUrl;
    }

    public String[] getAccessToken(String code) {
        OAuthService service = new ServiceBuilder().provider(FacebookApi.class).apiKey(getApiKey()).apiSecret(getApiSecret()).callback(getCallBackUrl()).build();
        Verifier verifier = new Verifier(code);
        Token accessToken = service.getAccessToken(EMPTY_TOKEN, verifier);
        return new String[] { accessToken.getToken(), accessToken.getSecret(), accessToken.getRawResponse() };
    }

    public void verfierAccessToken(String accessToken) {
        OAuthService service = new ServiceBuilder().provider(FacebookApi.class).apiKey(getApiKey()).apiSecret(getApiSecret()).callback(getCallBackUrl()).build();
        OAuthRequest request = new OAuthRequest(Verb.GET, getPROTECTED_RESOURCE_URL());
        service.signRequest(new Token(accessToken, getApiSecret()), request);
        request.send();
        // System.out.println(response.getCode());
        // System.out.println(response.getBody());
    }

    public String getPROTECTED_RESOURCE_URL() {
        return (String) cfg.get("facebook.protected_resource_url");
    }

    public String getApiKey() {
        return (String) cfg.get("facebook.apiKey");
    }

    public String getApiSecret() {
        return (String) cfg.get("facebook.apiSecret");
    }

    public String getCallBackUrl() {
        return (String) cfg.get("facebook.callBackUrl");
    }

    //
    public static void main(String[] args) {
        try {
            OAuthService service = new ServiceBuilder().provider(FacebookApi.class).apiKey("504604412891475").apiSecret("af295ca74435eca963a781200c79ac67").callback("http://southgatetestjsppage.com:8080/samplesocial/callback_fb").build();
            String authorizationUrl = service.getAuthorizationUrl(null);
            System.out.println(authorizationUrl);
            // String code =
            // "AQAwCbq8WZ1BrDJmniPjNv-9ttPsDA0YyMrf0vCxnPHwpgk8tfmydBvrZtkVNsYd963wVJhPWl770RvwLMZgnhzdJ1C1zSTDWn7kwdP6VQAqt-MoeT_sba776LopHQlUL6qEtLxiAtL096WCa7k6Nk0iAc1ij-1BZVx-61tjufYyNgmSHYHklRS1_1cU530JctvyIqFbvRbMfbg8MML1-H59#_=_";
            // // String token = a.getAccessToken(code);
            // // System.out.println(token);
            // String token =
            // "AAAHK717IUVMBAJhWdsmVCqYLZCbw7KIZCdvsK0NAsD7dDNrGnjCGm1QQHvuHvfRJVYEfSmYes0VsJpzWZCZBWKbbYoTv9aVjFomp96dkKgZDZD";
            // a.verfierAccessToken(token);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
