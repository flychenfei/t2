package com.britesnow.samplesocial.service;

import java.util.Map;

import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import com.britesnow.samplesocial.entity.SocialIdEntity;
import com.britesnow.samplesocial.entity.User;
import com.britesnow.samplesocial.oauth.OAuthServiceHelper;
import com.britesnow.samplesocial.oauth.OauthException;
import com.britesnow.samplesocial.oauth.ServiceType;
import com.britesnow.snow.util.JsonUtil;
import com.google.inject.Inject;
import com.google.inject.Singleton;


@Singleton
public class TwitterService {
	private OAuthService oAuthService;

	@Inject
	private TwitterAuthService twitterAuthService;
	
	@Inject
	public TwitterService(OAuthServiceHelper oauthServiceFactory) {
		oAuthService = oauthServiceFactory.getOauthService(ServiceType.Twitter);
	}
		
	public static final String USER_INFO = "https://api.twitter.com/1.1/users/show.json?user_id=%d";
	
	public static final String USER_TWITTER_ID = "https://api.twitter.com/1.1/account/verify_credentials.json";
	

	private Token getToken(User user) {
		SocialIdEntity socialEn = twitterAuthService.getSocialIdEntity(user.getId());
		if (socialEn != null) {
			return new Token(socialEn.getToken(), socialEn.getSecret());
		}
		throw new OauthException(oAuthService.getAuthorizationUrl(oAuthService.getRequestToken()));
	}
	
	public Map getUserInfo(User user) {
		OAuthRequest request = new OAuthRequest(Verb.GET, String.format(USER_INFO, getIdInTwitter(user).get("id")));
		oAuthService.signRequest(getToken(user), request);
	    Response response = request.send();
	    Map map = JsonUtil.toMapAndList(response.getBody());
	    return map;
	}
	
	private Map getIdInTwitter(User user) {
		OAuthRequest request = new OAuthRequest(Verb.GET, USER_TWITTER_ID);
		Token accessToken = getToken(user);
		oAuthService.signRequest(accessToken, request);
	    Response response = request.send();
	    Map map = JsonUtil.toMapAndList(response.getBody());
	    return map;
	}

}
