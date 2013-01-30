package com.britesnow.samplesocial.service;

import java.util.Map;
import java.util.Scanner;

import javax.inject.Inject;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

import com.britesnow.snow.web.binding.ApplicationProperties;
import com.google.inject.Singleton;

@Singleton
public class TwitterAuthService{
	@Inject
    @ApplicationProperties
    private Map cfg;
	
    
    public String getAuthorizationUrl() {
    	OAuthService service = new ServiceBuilder()
        	.provider(TwitterApi.class)
        		.apiKey("6icbcAXyZx67r8uTAUM5Qw")
        			.apiSecret("SCCAdUUc6LXxiazxH3N0QfpNUvlUy84mZ2XZKiv39s")
        				.build();
		
		Token requestToken = service.getRequestToken();
		String url = service.getAuthorizationUrl(requestToken);
		return url;
    	
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