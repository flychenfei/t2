package com.britesnow.samplesocial.service;

import com.britesnow.samplesocial.dao.SocialIdEntityDao;
import com.britesnow.samplesocial.entity.Service;
import com.britesnow.samplesocial.entity.SocialIdEntity;
import com.google.inject.Inject;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.FacebookApi;
import org.scribe.model.*;
import org.scribe.oauth.OAuthService;

public class FacebookAuthService implements AuthService {
    @Inject
    private SocialIdEntityDao   socialIdEntityDao;

    private Service             serivce                = Service.FaceBook;
    private static final String PROTECTED_RESOURCE_URL = "https://graph.facebook.com/me";
    private static final Token  EMPTY_TOKEN            = null;
    private static String       apiKey                 = "504604412891475";
    private static String       apiSecret              = "af295ca74435eca963a781200c79ac67";
    private static String       callBackUrl            = "http://southgatetestjsppage.com:8080/samplesocial/callback_fb";

    @Override
    public SocialIdEntity getSocialIdEntity(Long userId) {
        return socialIdEntityDao.getSocialdentity(userId, serivce);
    }

    public String getAuthorizationUrl() {
        OAuthService service = new ServiceBuilder().provider(FacebookApi.class).apiKey(apiKey).apiSecret(apiSecret).callback(callBackUrl).build();
        String authorizationUrl = service.getAuthorizationUrl(EMPTY_TOKEN);
        System.out.println(authorizationUrl);
        return authorizationUrl;
    }

    public String[] getAccessToken(String code) {
        OAuthService service = new ServiceBuilder().provider(FacebookApi.class).apiKey(apiKey).apiSecret(apiSecret).callback(callBackUrl).build();
        Verifier verifier = new Verifier(code);
        Token accessToken = service.getAccessToken(EMPTY_TOKEN, verifier);
        return new String[] { accessToken.getToken(), accessToken.getSecret(), accessToken.getRawResponse() };
    }

    public void verfierAccessToken(String accessToken) {
        OAuthService service = new ServiceBuilder().provider(FacebookApi.class).apiKey(apiKey).apiSecret(apiSecret).callback(callBackUrl).build();
        OAuthRequest request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL);
        service.signRequest(new Token(accessToken, apiSecret), request);
        Response response = request.send();
        System.out.println(response.getCode());
        System.out.println(response.getBody());
    }
    //
    // public static void main(String[] args) {
    // try {
    // FacebookAuthService a = new FacebookAuthService();
    // // String url = a.getAuthorizationUrl();
    // // System.out.println(url);
    // String code =
    // "AQAwCbq8WZ1BrDJmniPjNv-9ttPsDA0YyMrf0vCxnPHwpgk8tfmydBvrZtkVNsYd963wVJhPWl770RvwLMZgnhzdJ1C1zSTDWn7kwdP6VQAqt-MoeT_sba776LopHQlUL6qEtLxiAtL096WCa7k6Nk0iAc1ij-1BZVx-61tjufYyNgmSHYHklRS1_1cU530JctvyIqFbvRbMfbg8MML1-H59#_=_";
    // // String token = a.getAccessToken(code);
    // // System.out.println(token);
    // String token =
    // "AAAHK717IUVMBAJhWdsmVCqYLZCbw7KIZCdvsK0NAsD7dDNrGnjCGm1QQHvuHvfRJVYEfSmYes0VsJpzWZCZBWKbbYoTv9aVjFomp96dkKgZDZD";
    // a.verfierAccessToken(token);
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // }
}
