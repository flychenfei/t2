package com.britesnow.samplesocial.service;

import java.util.Map;

import javax.inject.Inject;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.TwitterApi;
import org.scribe.oauth.OAuthService;

import com.britesnow.snow.web.binding.ApplicationProperties;
import com.google.inject.Singleton;

@Singleton
public class TwitterAuthService{
	@Inject
    @ApplicationProperties
    private Map cfg;
	
//    public String getAuthorizationUrl() {
//        OAuthService service = new ServiceBuilder().provider(TwitterApi.class).apiKey(getApiKey()).apiSecret(getApiSecret()).callback(getCallBackUrl()).build();
////        Token requestToken = service.getRequestToken();
//        String authorizationUrl = service.getAuthorizationUrl(null);
//        System.out.println(authorizationUrl);
//        return authorizationUrl;
//    }
    
    public String getAuthorizationUrl() {
    	 OAuthService service = new ServiceBuilder().provider(TwitterApi.class).apiKey(getApiKey()).apiSecret(getApiSecret()).callback(getCallBackUrl()).build();
    	 String authorizationUrl = service.getAuthorizationUrl(null);
    	 System.out.println(authorizationUrl);
    	 return authorizationUrl;
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