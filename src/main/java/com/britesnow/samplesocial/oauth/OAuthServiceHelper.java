package com.britesnow.samplesocial.oauth;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.*;
import org.scribe.model.SignatureType;
import org.scribe.oauth.OAuthService;

import com.britesnow.samplesocial.oauth.api.GitHubApi;
import com.britesnow.samplesocial.oauth.api.GoogleApi20;
import com.britesnow.samplesocial.oauth.api.SalesForceApi;
import com.britesnow.snow.web.binding.ApplicationProperties;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class OAuthServiceHelper {
    private final Map          appconfig;

    public static final String EMAIL_ENDPOINT   = "https://www.googleapis.com/userinfo/email";
    public static final String PROFILE_ENDPOINT = "https://www.googleapis.com/oauth2/v1/userinfo";

    @Inject
    public OAuthServiceHelper(@ApplicationProperties Map appConfig) {
        this.appconfig = appConfig;
    }

    public OAuthService getOauthService(ServiceType serviceType) {
        OAuthService oauthService = null;
        if (serviceType == ServiceType.Google) {
            oauthService = this.getGoogleOAuthService();
        } else if (serviceType == ServiceType.FaceBook) {
            oauthService = this.getFaceBookOAuthService();
        } else if (serviceType == ServiceType.LinkedIn) {
            oauthService = this.getLinkedInOAuthService();
        } else if (serviceType == ServiceType.SalesForce) {
            oauthService = this.getSalesForceOAuthService();
        } else if (serviceType == ServiceType.Github) {
            oauthService = this.getGitHubOAuthService();
        } else if (serviceType == ServiceType.Twitter) {
            oauthService = this.getTwitterOAuthService();
        } else if (serviceType == ServiceType.Live) {
            oauthService = this.getLiveOauthService();
        } else if (serviceType == ServiceType.Foursquare) {
            oauthService = this.getFoursquare();
        } else if (serviceType == ServiceType.Dropbox) {
            oauthService = this.getDropbox();
        } else if (serviceType == ServiceType.Yahoo) {
            oauthService = this.getYahoo();
        }

        return oauthService;
    }

    private OAuthService getYahoo() {
        String prefix = "yahoo";
        String clientId = (String) appconfig.get(prefix + ".app_key");
        String secret = (String) appconfig.get(prefix + ".app_secret");
        String callback = (String) appconfig.get(prefix + ".oauth_callback");
        ServiceBuilder builder = new ServiceBuilder().provider(YahooApi.class).apiKey(clientId).apiSecret(secret);
        if (callback != null) {
            builder.callback(callback);
        }
        builder.signatureType(SignatureType.Header);
        return builder.build();
    }

    private OAuthService getFoursquare() {
        String prefix = "foursquare";
        String clientId = (String) appconfig.get(prefix + ".client_id");
        String secret = (String) appconfig.get(prefix + ".secret");
        String callback = (String) appconfig.get(prefix + ".callback");
        String scope = (String) appconfig.get(prefix + ".scope");
        ServiceBuilder builder = new ServiceBuilder().provider(Foursquare2Api.class).apiKey(clientId).apiSecret(secret);
        if (callback != null) {
            builder.callback(callback);
        }
        if (scope != null) {
            builder.scope(scope);
        }
        return builder.build();
    }

    private OAuthService getTwitterOAuthService() {
        String prefix = "twitter";
        String clientId = (String) appconfig.get(prefix + ".apiKey");
        String secret = (String) appconfig.get(prefix + ".apiSecret");
        String callback = (String) appconfig.get(prefix + ".callBackUrl");
        String scope = (String) appconfig.get(prefix + ".scope");
        ServiceBuilder builder = new ServiceBuilder().provider(TwitterApi.class).apiKey(clientId).apiSecret(secret);
        if (callback != null) {
            builder.callback(callback);
        }

        if (scope != null) {
            builder.scope(scope);
        }
        return builder.build();
    }

    private OAuthService getGoogleOAuthService() {
        String prefix = "google";
        String clientId = (String) appconfig.get(prefix + ".client_id");
        String secret = (String) appconfig.get(prefix + ".secret");
        String callback = (String) appconfig.get(prefix + ".callback");
        String scope = (String) appconfig.get(prefix + ".scope");
        ServiceBuilder builder = new ServiceBuilder().provider(GoogleApi20.class).apiKey(clientId).apiSecret(secret);
        // builder.grantType(OAuthConstants.GRANT_TYPE_AUTHORIZATION_CODE);
        if (callback != null) {
            builder.callback(callback);
        }
        if (scope != null) {
            builder.scope(scope);
        }
        return builder.build();
    }

    private OAuthService getFaceBookOAuthService() {
        String prefix = "facebook";
        String clientId = (String) appconfig.get(prefix + ".apiKey");
        String secret = (String) appconfig.get(prefix + ".apiSecret");
        String callback = (String) appconfig.get(prefix + ".callBackUrl");
        String scope = (String) appconfig.get(prefix + ".scope");
        ServiceBuilder builder = new ServiceBuilder().provider(FacebookApi.class).apiKey(clientId).apiSecret(secret).callback(callback);
        if (StringUtils.isNotBlank(scope)) {
            builder.scope(scope);
        }
        return builder.build();
    }

    private OAuthService getLinkedInOAuthService() {
        String prefix = "linkedin";
        String clientId = (String) appconfig.get(prefix + ".client_id");
        String secret = (String) appconfig.get(prefix + ".secret");
        String callback = (String) appconfig.get(prefix + ".callback");
        String scope = (String) appconfig.get(prefix + ".scope");
        ServiceBuilder builder = new ServiceBuilder().provider(LinkedInApi.class).apiKey(clientId).apiSecret(secret);
        // builder.grantType(OAuthConstants.GRANT_TYPE_AUTHORIZATION_CODE);
        if (callback != null) {
            builder.callback(callback);
        }
        if (scope != null) {
            builder.scope(scope);
        }
        return builder.build();
    }

    private OAuthService getSalesForceOAuthService() {
        String prefix = "salesforce";
        String clientId = (String) appconfig.get(prefix + ".apiKey");
        String secret = (String) appconfig.get(prefix + ".apiSecret");
        String callback = (String) appconfig.get(prefix + ".callbackUrl");
        ServiceBuilder builder = new ServiceBuilder().provider(SalesForceApi.class).apiKey(clientId).apiSecret(secret).callback(callback);
        return builder.build();
    }

    private OAuthService getGitHubOAuthService() {
        String prefix = "github";
        String clientId = (String) appconfig.get(prefix + ".client_id");
        String secret = (String) appconfig.get(prefix + ".secret");
        String callback = (String) appconfig.get(prefix + ".callback");
        String scope = (String) appconfig.get(prefix + ".scope");
        ServiceBuilder builder = new ServiceBuilder().provider(GitHubApi.class).apiKey(clientId).apiSecret(secret);
        // builder.grantType(OAuthConstants.GRANT_TYPE_AUTHORIZATION_CODE);
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
        String clientId = (String) appconfig.get(prefix + ".apiKey");
        String secret = (String) appconfig.get(prefix + ".apiSecret");
        String callback = (String) appconfig.get(prefix + ".callBackUrl");
        String scope = (String) appconfig.get(prefix + ".scope");
        ServiceBuilder builder = new ServiceBuilder().provider(LiveApi.class).apiKey(clientId).apiSecret(secret);
        // builder.grantType(OAuthConstants.GRANT_TYPE_AUTHORIZATION_CODE);
        if (callback != null) {
            builder.callback(callback);
        }
        if (scope != null) {
            builder.scope(scope);
        }
        return builder.build();
    }

    public OAuthService getDropbox() {
        String prefix = "dropbox";
        String clientId = (String) appconfig.get(prefix + ".app_key");
        String secret = (String) appconfig.get(prefix + ".app_secret");
        String callback = (String) appconfig.get(prefix + ".callBackUrl");
        ServiceBuilder builder = new ServiceBuilder().provider(DropBoxApi.class).apiKey(clientId).apiSecret(secret);
        if (callback != null) {
            builder.callback(callback);
        }
        builder.signatureType(SignatureType.Header);
        return builder.build();
    }
}
