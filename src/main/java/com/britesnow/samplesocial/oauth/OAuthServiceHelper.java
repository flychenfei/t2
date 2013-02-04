package com.britesnow.samplesocial.oauth;

import java.util.Map;

import com.britesnow.samplesocial.oauth.api.GitHubApi;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.FacebookApi;
import org.scribe.builder.api.LinkedInApi;
import org.scribe.builder.api.LiveApi;
import org.scribe.builder.api.TwitterApi;
import org.scribe.oauth.OAuthService;

import com.britesnow.samplesocial.oauth.api.GoogleApi20;
import com.britesnow.samplesocial.oauth.api.SalesForceApi;
import com.britesnow.snow.web.binding.ApplicationProperties;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class OAuthServiceHelper {
    private final Map appconfig;

    public static final String EMAIL_ENDPOINT = "https://www.googleapis.com/userinfo/email";
    public static final String PROFILE_ENDPOINT = "https://www.googleapis.com/oauth2/v1/userinfo";

    @Inject
    public OAuthServiceHelper(@ApplicationProperties Map appConfig) {
        this.appconfig = appConfig;
    }

    public OAuthService getOauthService(ServiceType serviceType) {
        OAuthService oauthService = null;
        if (serviceType == ServiceType.Google) {
            oauthService = this.getGoogleOAuthService();
        }else if(serviceType == ServiceType.FaceBook){
            oauthService = this.getFaceBookOAuthService();
        }else if(serviceType == ServiceType.LinkedIn){
            oauthService = this.getLinkedInOAuthService();
        }else if(serviceType == ServiceType.SalesForce){
            oauthService = this.getSalesForceOAuthService();
        }else if(serviceType == ServiceType.Github){
            oauthService = this.getGitHubOAuthService();
        }else if(serviceType == ServiceType.Twitter){
            oauthService = this.getTwitterOAuthService();
        }else if (serviceType == ServiceType.Live) {
            oauthService = this.getLiveOauthService();
        }
        
        return oauthService;
    }
    
    private OAuthService getTwitterOAuthService() {
    	 String prefix = "twitter";
         String clientId = (String) appconfig.get(prefix+".apiKey");
         String secret = (String) appconfig.get(prefix+".apiSecret");
         String callback = (String) appconfig.get(prefix+".callBackUrl");
         ServiceBuilder builder = new ServiceBuilder().provider(TwitterApi.class).apiKey(clientId).apiSecret(secret).callback(callback);
         return builder.build();
	}

	private OAuthService getGoogleOAuthService(){
        String prefix = "google";
        String clientId = (String) appconfig.get(prefix+".client_id");
        String secret = (String) appconfig.get(prefix+".secret");
        String callback = (String) appconfig.get(prefix+".callback");
        String scope = (String) appconfig.get(prefix+".scope");
        ServiceBuilder builder = new ServiceBuilder().provider(GoogleApi20.class).apiKey(clientId).apiSecret(secret);
        //builder.grantType(OAuthConstants.GRANT_TYPE_AUTHORIZATION_CODE);
        if (callback != null) {
            builder.callback(callback);
        }
        if (scope != null) {
            builder.scope(scope);
        }
        return builder.build();
    }
    
    private OAuthService getFaceBookOAuthService(){
        String prefix = "facebook";
        String clientId = (String) appconfig.get(prefix+".apiKey");
        String secret = (String) appconfig.get(prefix+".apiSecret");
        String callback = (String) appconfig.get(prefix+".callBackUrl");
        String scope =  "publish_actions";
        ServiceBuilder builder = new ServiceBuilder().provider(FacebookApi.class).apiKey(clientId).apiSecret(secret).scope(scope).callback(callback);
        return builder.build();
    }
    
    private OAuthService getLinkedInOAuthService(){
        String prefix = "linkedin";
        String clientId = (String) appconfig.get(prefix+".client_id");
        String secret = (String) appconfig.get(prefix+".secret");
        String callback = (String) appconfig.get(prefix+".callback");
        String scope = (String) appconfig.get(prefix+".scope");
        ServiceBuilder builder = new ServiceBuilder().provider(LinkedInApi.class).apiKey(clientId).apiSecret(secret);
        //builder.grantType(OAuthConstants.GRANT_TYPE_AUTHORIZATION_CODE);
        if (callback != null) {
            builder.callback(callback);
        }
        if (scope != null) {
            builder.scope(scope);
        }
        return builder.build();
    }
    
    private OAuthService getSalesForceOAuthService(){
        String prefix = "salesforce";
        String clientId = (String) appconfig.get(prefix+".apiKey");
        String secret = (String) appconfig.get(prefix+".apiSecret");
        String callback = (String) appconfig.get(prefix+".callbackUrl");
        ServiceBuilder builder = new ServiceBuilder().provider(SalesForceApi.class).apiKey(clientId).apiSecret(secret).callback(callback);
        return builder.build();
    }
    
    private OAuthService getGitHubOAuthService(){
        String prefix = "github";
        String clientId = (String) appconfig.get(prefix+".client_id");
        String secret = (String) appconfig.get(prefix+".secret");
        String callback = (String) appconfig.get(prefix+".callback");
        String scope = (String) appconfig.get(prefix+".scope");
        ServiceBuilder builder = new ServiceBuilder().provider(GitHubApi.class).apiKey(clientId).apiSecret(secret);
        //builder.grantType(OAuthConstants.GRANT_TYPE_AUTHORIZATION_CODE);
        if (callback != null) {
            builder.callback(callback);
        }
        if (scope != null) {
            builder.scope(scope);
        }
        return builder.build();
    }

    public OAuthService getLiveOauthService() {
        String prefix = "live";
        String clientId = (String) appconfig.get(prefix+".apiKey");
        String secret = (String) appconfig.get(prefix+".apiSecret");
        String callback = (String) appconfig.get(prefix+".callBackUrl");
        String scope = (String) appconfig.get(prefix+".scope");
        ServiceBuilder builder = new ServiceBuilder().provider(LiveApi.class).apiKey(clientId).apiSecret(secret);
        //builder.grantType(OAuthConstants.GRANT_TYPE_AUTHORIZATION_CODE);
        if (callback != null) {
            builder.callback(callback);
        }
        if (scope != null) {
            builder.scope(scope);
        }
        return builder.build();
    }
}
