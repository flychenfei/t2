package com.britesnow.samplesocial.service;

import static org.scribe.model.OAuthConstants.EMPTY_TOKEN;

import java.util.Map;

import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import com.britesnow.samplesocial.entity.SocialIdEntity;
import com.britesnow.samplesocial.entity.User;
import com.britesnow.samplesocial.oauth.OAuthServiceHelper;
import com.britesnow.samplesocial.oauth.ServiceType;
import com.britesnow.snow.web.binding.ApplicationProperties;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class GithubService {
	private final Map appConfig;

	private static String TOKEN_URL = "https://github.com/login/oauth/access_token";

	@Inject
	private YaoGithubAuthService yaoGithubAuthService;

	private OAuthService oAuthService;

	@Inject
	public GithubService(@ApplicationProperties Map appConfig,OAuthServiceHelper oauthServiceHelper) {
		this.appConfig = appConfig;
		oAuthService = oauthServiceHelper
				.getOauthService(ServiceType.Github);
	}

	public Token getToken(User user) {
		SocialIdEntity soId = yaoGithubAuthService.getSocialIdEntity(user
				.getId());
		return new Token(soId.getToken(), soId.getSecret());
	}

	private OAuthRequest createRequest(Verb verb, String url) {
		OAuthRequest request = new OAuthRequest(verb, url);
		 request.addHeader("x-li-format","json");
		return request;
	}

	public String getUserInfo(User user, String code) {
		OAuthRequest request = createRequest(Verb.POST, TOKEN_URL);
		 Verifier verifier = new Verifier(code);
	     Token accessToken = oAuthService.getAccessToken(EMPTY_TOKEN, verifier);
		System.out.println( accessToken.getToken());
		return null;
	}
}
