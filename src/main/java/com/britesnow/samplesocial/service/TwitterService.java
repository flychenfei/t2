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
	
	public static final String USER_TWITTER_ID = "https://api.twitter.com/1.1/account/verify_credentials.json";
	public static final String USER_INFO = "https://api.twitter.com/1.1/users/show.json?user_id=%d";
	
	//Timelines
	public static final String MENTION_TIMELINE = "https://api.twitter.com/1.1/statuses/mentions_timeline.json?count=2&since_id=14927799";
	public static final String USER_TIMELINE = "https://api.twitter.com/1.1/statuses/user_timeline.json?user_id=%d";
	public static final String TWITTER_TIMELINE = "https://api.twitter.com/1.1/statuses/home_timeline.json";
	public static final String RETWEET_OF_ME = "https://api.twitter.com/1.1/statuses/retweets_of_me.json";
	
	//Tweets
	public static final String RETWEET_BY_ID = "https://api.twitter.com/1.1/statuses/retweets/%s.json";
	public static final String STATUS_BY_ID = "https://api.twitter.com/1.1/statuses/show.json?id=%s";
	public static final String DESTORY_TWEET = "https://api.twitter.com/1.1/statuses/destroy/%s.json";
	public static final String POST_STATUS = "https://api.twitter.com/1.1/statuses/update.json";
	public static final String RETWEET = "https://api.twitter.com/1.1/statuses/retweet/%s.json";
	
	//Search
	public static final String SEARECH_TWEETS = "https://api.twitter.com/1.1/search/tweets.json?q=%s";
	
	//Direct Messages
	public static final String DIRECT_MSG = "https://api.twitter.com/1.1/direct_messages.json?count=5";
	public static final String SHOW_MSG = "https://api.twitter.com/1.1/direct_messages/show.json?id=%s";
	public static final String GET_SEND_MSG = "https://api.twitter.com/1.1/direct_messages/sent.json";
	public static final String SEND_MSG = "https://api.twitter.com/1.1/direct_messages/new.json";
	
	//Favorites
	public static final String FAVORITE = "https://api.twitter.com/1.1/favorites/create.json";
	
	//Suggested Users
	public static final String SUGGESTIONS = "https://api.twitter.com/1.1/users/suggestions.json";
	
	
	
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
	
	//Timeline
	public String getTimeline(User user) {
		OAuthRequest request = new OAuthRequest(Verb.GET, TWITTER_TIMELINE);
		oAuthService.signRequest(getToken(user), request);
	    Response response = request.send();
	    return response.getBody();
	}
	
	public String getRetweets(User user) {
		OAuthRequest request = new OAuthRequest(Verb.GET, RETWEET_OF_ME);
		oAuthService.signRequest(getToken(user), request);
	    Response response = request.send();
	    return response.getBody();
	}
	
	public String getMentionTimeline(User user) {
		OAuthRequest request = new OAuthRequest(Verb.GET, MENTION_TIMELINE);
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

	//Tweets
	//GET statuses/retweets/:id
	public String getRetweetById(User user, String tweet_id) {
		OAuthRequest request = new OAuthRequest(Verb.GET, String.format(RETWEET_BY_ID, tweet_id));
		Token accessToken = getToken(user);
		oAuthService.signRequest(accessToken, request);
	    Response response = request.send();
		return response.getBody();
	}
	
	//GET statuses/show/:id
	public Map getStatusById(User user, String tweet_id) {
		OAuthRequest request = new OAuthRequest(Verb.GET, String.format(STATUS_BY_ID, tweet_id));
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
		System.out.println(String.format(DESTORY_TWEET, tweet_id));
		oAuthService.signRequest(getToken(user), request);
	    Response response = request.send();
	    Map map = JsonUtil.toMapAndList(response.getBody());
	    return map;
	}
	
	//Search
	//GET search/tweets
	public Map searchTweets(User user, String query) {
		OAuthRequest request = new OAuthRequest(Verb.GET, String.format(SEARECH_TWEETS, query));
		Token accessToken = getToken(user);
		oAuthService.signRequest(accessToken, request);
	    Response response = request.send();
	    Map map = JsonUtil.toMapAndList(response.getBody());
		return map;
	}
	
	//Direct Messages
	//GET direct_messages
	public String getDirectMsg(User user) {
		OAuthRequest request = new OAuthRequest(Verb.GET, DIRECT_MSG);
		Token accessToken = getToken(user);
		oAuthService.signRequest(accessToken, request);
	    Response response = request.send();
		return response.getBody();
	}
	
	//GET direct_messages/sent
	public String getSendMsg(User user) {
		OAuthRequest request = new OAuthRequest(Verb.GET, GET_SEND_MSG);
		Token accessToken = getToken(user);
		oAuthService.signRequest(accessToken, request);
	    Response response = request.send();
		return response.getBody();
	}
	
	//GET direct_messages/show
	public String showMsg(User user,String msg_id) {
		OAuthRequest request = new OAuthRequest(Verb.GET, String.format(SHOW_MSG, msg_id));
		System.out.println("msg_id:" + String.format(SHOW_MSG, msg_id));
		Token accessToken = getToken(user);
		oAuthService.signRequest(accessToken, request);
	    Response response = request.send();
		return response.getBody();
	}
	
	//POST direct_messages
	public Map sendMsg(User user,String screen_name, String text) {
		OAuthRequest request = new OAuthRequest(Verb.POST, SEND_MSG);
		request.addBodyParameter("screen_name", screen_name);
		request.addBodyParameter("text", text);
		Token accessToken = getToken(user);
		oAuthService.signRequest(accessToken, request);
		Response response = request.send();
	    Map map = JsonUtil.toMapAndList(response.getBody());
	    return map;
	}
	
	//Suggested Users
	public String getSuggestions(User user) {
		OAuthRequest request = new OAuthRequest(Verb.GET, SUGGESTIONS);
		Token accessToken = getToken(user);
		oAuthService.signRequest(accessToken, request);
	    Response response = request.send();
		return response.getBody();
	}
	
	//Favorites
	public Map favorite(User user, String tweet_id) {
		OAuthRequest request = new OAuthRequest(Verb.POST, FAVORITE);
		request.addBodyParameter("id", tweet_id);
		oAuthService.signRequest(getToken(user), request);
	    Response response = request.send();
	    Map map = JsonUtil.toMapAndList(response.getBody());
	    return map;
	}

}
