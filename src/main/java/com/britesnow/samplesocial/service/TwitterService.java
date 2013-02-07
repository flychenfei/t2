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
	
	public static final String TWITTER_TIMELINE = "https://api.twitter.com/1.1/statuses/home_timeline.json";
	
	public static final String USER_TWITTER_ID = "https://api.twitter.com/1.1/account/verify_credentials.json";
	
	public static final String POST_STATUS = "https://api.twitter.com/1.1/statuses/update.json";
	
	public static final String RETWEET = "https://api.twitter.com/1.1/statuses/retweet/%s.json";
	
	public static final String FAVORITE = "https://api.twitter.com/1.1/favorites/create.json";
	
	public static final String USER_TIMELINE = "https://api.twitter.com/1.1/statuses/user_timeline.json?user_id=%d";
	
	public static final String DESTORY_TWEET = "https://api.twitter.com/1.1/statuses/destroy/%s.json";

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
	
	public String getTimeline(User user) {
		OAuthRequest request = new OAuthRequest(Verb.GET, TWITTER_TIMELINE);
		oAuthService.signRequest(getToken(user), request);
	    Response response = request.send();
	    return response.getBody();
	}
	
	public String getUserTimeline(User user) {
		OAuthRequest request = new OAuthRequest(Verb.GET, String.format(USER_TIMELINE, getIdInTwitter(user).get("id")));
		oAuthService.signRequest(getToken(user), request);
	    Response response = request.send();
	    return response.getBody();
	}
	
	private Map getIdInTwitter(User user) {
		OAuthRequest request = new OAuthRequest(Verb.GET, USER_TWITTER_ID);
		Token accessToken = getToken(user);
		oAuthService.signRequest(accessToken, request);
	    Response response = request.send();
	   
	    Map map = JsonUtil.toMapAndList(response.getBody());
	    return map;
	}

	public String postStatus(User user, String status) {
		OAuthRequest request = new OAuthRequest(Verb.POST, POST_STATUS);
		request.addBodyParameter("status", status);
		Token accessToken = getToken(user);
		oAuthService.signRequest(accessToken, request);
	    Response response = request.send();
		return response.getBody();
	}

	public Map retweet(User user, String tweet_id) {
		OAuthRequest request = new OAuthRequest(Verb.POST, String.format(RETWEET, tweet_id));
		request.addBodyParameter("id", tweet_id);
		oAuthService.signRequest(getToken(user), request);
	    Response response = request.send();
	    Map map = JsonUtil.toMapAndList(response.getBody());
	    return map;
	}
	
	public Map destroyTweet(User user, String tweet_id) {
		OAuthRequest request = new OAuthRequest(Verb.POST, String.format(DESTORY_TWEET, tweet_id));
		request.addBodyParameter("id", tweet_id);
		oAuthService.signRequest(getToken(user), request);
	    Response response = request.send();
	    Map map = JsonUtil.toMapAndList(response.getBody());
	    return map;
	}

	
	public Map favorite(User user, String tweet_id) {
		OAuthRequest request = new OAuthRequest(Verb.POST, FAVORITE);
		request.addBodyParameter("id", tweet_id);
		oAuthService.signRequest(getToken(user), request);
	    Response response = request.send();
	    Map map = JsonUtil.toMapAndList(response.getBody());
	    return map;
	}

}
