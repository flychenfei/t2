package com.britesnow.samplesocial.oauth;


import org.scribe.builder.api.Api;
import org.scribe.builder.api.FacebookApi;
import org.scribe.builder.api.LinkedInApi;

import com.britesnow.samplesocial.oauth.api.GitHubApi;
import com.britesnow.samplesocial.oauth.api.GoogleApi20;

public enum OAuthType {
    FB("facebook", FacebookApi.class), GH("github", GitHubApi.class),
    GOOGLE("google", GoogleApi20.class), LINKEDIN("linkedin", LinkedInApi.class);
    private String key;
    private Class<? extends Api> apiClass;

    private OAuthType(String key, Class<? extends Api> apiClass) {
        this.key = key;
        this.apiClass = apiClass;
    }

    public String getKey() {
        return key;
    }

    public Class<? extends Api> getApiClass() {
        return apiClass;
    }
}