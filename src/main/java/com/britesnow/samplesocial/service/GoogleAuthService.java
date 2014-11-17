package com.britesnow.samplesocial.service;

import static org.scribe.model.OAuthConstants.EMPTY_TOKEN;

import java.util.HashMap;
import java.util.Map;

import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import com.britesnow.samplesocial.manager.OAuthManager;
import com.britesnow.samplesocial.model.SocialIdEntity;
import com.britesnow.samplesocial.oauth.OAuthServiceHelper;
import com.britesnow.samplesocial.oauth.OauthException;
import com.britesnow.samplesocial.oauth.ServiceType;
import com.britesnow.snow.util.JsonUtil;
import com.google.inject.Inject;
import com.google.inject.Singleton;


@Singleton
public class GoogleAuthService implements AuthService {

    private OAuthService oAuthService;
    @Inject
    private OAuthManager oAuthManager;
    
    @Inject
    public GoogleAuthService(OAuthServiceHelper oauthServiceHelper) {
        oAuthService = oauthServiceHelper.getOauthService(ServiceType.Google);
    }

    
    public SocialIdEntity getSocialIdEntity() {
		SocialIdEntity socialId = oAuthManager.getSocialIdEntityfromSession(ServiceType.Google);
        if(socialId == null){
        	//if result is null, need redo auth
        	throw new OauthException(getAuthorizationUrl());
        }
        return socialId;
    }

    public String getAuthorizationUrl() {
        return oAuthService.getAuthorizationUrl(EMPTY_TOKEN);
    }

    public String updateAccessToken(String verifierCode) {
    	
        Verifier verifier = new Verifier(verifierCode);
        Token accessToken = oAuthService.getAccessToken(EMPTY_TOKEN, verifier);
        if (accessToken.getToken() != null) {
            //get userinfo
            OAuthRequest request = new OAuthRequest(Verb.GET, OAuthServiceHelper.PROFILE_ENDPOINT);
            
            request.addHeader("x-li-format","json");
            oAuthService.signRequest(accessToken, request);
            Response response = request.send();
            Map profile = JsonUtil.toMapAndList(response.getBody());
            
            //todo extract userinfo
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("secret", accessToken.getSecret());
            map.put("access_token", accessToken.getToken());
            map.put("email", (String) profile.get("email"));
            oAuthManager.setInfo(ServiceType.Google, map);
            
        } else{
            throw new OauthException(getAuthorizationUrl());
        }
        return  accessToken.getToken();

    }
    
    
}
