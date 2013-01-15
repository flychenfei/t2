package com.britesnow.samplesocial.oauth.api;


import org.scribe.builder.api.DefaultApi20;
import org.scribe.model.OAuthConfig;
import org.scribe.utils.OAuthEncoder;



public class GitHubApi extends DefaultApi20 {
    private static final String AUTHORIZE_URL = "https://github.com/login/oauth/authorize?client_id=%s";
    private static final String ACCESS_TOKEN_URL = "https://github.com/login/oauth/access_token";
//    private static final String AUTHORIZE_URL = "https://www.lovefilm.com/activate?oauth_token=%s";
    @Override
    public String getAccessTokenEndpoint() {
        return ACCESS_TOKEN_URL;
    }

    @Override
    public String getAuthorizationUrl(OAuthConfig oAuthConfig) {
        String url = String.format(AUTHORIZE_URL, oAuthConfig.getApiKey());
        if (oAuthConfig.getCallback() != null) {
            url = url + "&redirect_uri=" + OAuthEncoder.encode(oAuthConfig.getCallback());
        }
        if (oAuthConfig.hasScope()) {
            url += "&scope=" + oAuthConfig.getScope();
        }
        return url;
    }

}
