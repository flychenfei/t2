package com.britesnow.samplesocial.service;

import static org.scribe.model.OAuthConstants.EMPTY_TOKEN;

import org.eclipse.egit.github.core.client.GitHubClient;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import com.britesnow.samplesocial.dao.SocialIdEntityDao;
import com.britesnow.samplesocial.entity.SocialIdEntity;
import com.britesnow.samplesocial.entity.User;
import com.britesnow.samplesocial.oauth.OAuthServiceHelper;
import com.britesnow.samplesocial.oauth.OauthException;
import com.britesnow.samplesocial.oauth.ServiceType;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class YaoGithubAuthService implements AuthService{

    @Inject
    private SocialIdEntityDao socialIdEntityDao;
    private OAuthService oAuthService;

    @Inject
    public YaoGithubAuthService(OAuthServiceHelper oauthServiceHelper) {
        oAuthService = oauthServiceHelper.getOauthService(ServiceType.Github);
    }

    @Override
    public SocialIdEntity getSocialIdEntity(Long userId) {
    	SocialIdEntity soId = socialIdEntityDao.getSocialdentity(userId, ServiceType.Github);
    	if(soId==null)
    		 throw new OauthException(getAuthorizationUrl());
        return soId;
    }

    public String getAuthorizationUrl() {
        return oAuthService.getAuthorizationUrl(EMPTY_TOKEN);
    }

    public OAuthRequest createRequest(Verb verb, String url) {
		OAuthRequest request = new OAuthRequest(verb, url);
		 request.addHeader("x-li-format","json");
		return request;
	}
    
    public Token getToken(User user) {
		SocialIdEntity soId = getSocialIdEntity(user.getId());
		return new Token(soId.getToken(), soId.getSecret());
	}
    
    public GitHubClient createClient(User user){
		GitHubClient client = new GitHubClient();
		client.setOAuth2Token(getToken(user).getToken());
		client.setUserAgent("GitHubJava/2.1.0");
		return client;
    }
}
