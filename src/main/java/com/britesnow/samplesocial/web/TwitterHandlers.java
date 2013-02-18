package com.britesnow.samplesocial.web;


import java.util.Map;

import com.britesnow.samplesocial.entity.User;
import com.britesnow.samplesocial.service.TwitterService;
import com.britesnow.snow.web.RequestContext;
import com.britesnow.snow.web.param.annotation.WebParam;
import com.britesnow.snow.web.param.annotation.WebUser;
import com.britesnow.snow.web.rest.annotation.WebGet;
import com.britesnow.snow.web.rest.annotation.WebPost;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class TwitterHandlers {
    @Inject
    private TwitterService twitterService;

    @WebGet("/twitter/getUserInfo")
    public WebResponse getUserInfo(@WebUser User user, RequestContext rc) throws Exception {
    	Map userInfo = twitterService.getUserInfo(user);
    	WebResponse response = WebResponse.success(userInfo);
    	return response;
    }
    
    @WebGet("/twitter/getTimeline")
    public WebResponse getTimeline(@WebUser User user, RequestContext rc) throws Exception {
    	String timeline = twitterService.getTimeline(user);
    	WebResponse response = WebResponse.success(timeline);
    	return response;
    }
    
    
    @WebGet("/twitter/getRetweets")
    public WebResponse getRetweets(@WebUser User user, RequestContext rc) throws Exception {
    	String retweets = twitterService.getRetweets(user);
    	WebResponse response = WebResponse.success(retweets);
    	return response;
    }
    
    @WebGet("/twitter/getMentionTimeline")
    public WebResponse getMentionTimeline(@WebUser User user, RequestContext rc) throws Exception {
    	String timeline = twitterService.getMentionTimeline(user);
    	WebResponse response = WebResponse.success(timeline);
    	return response;
    }
    
    @WebGet("/twitter/getUserTimeline")
    public WebResponse getUserTimeline(@WebUser User user, RequestContext rc) throws Exception {
    	String timeline = twitterService.getUserTimeline(user);
    	WebResponse response = WebResponse.success(timeline);
    	return response;
    }
    
    @WebPost("/twitter/postStatus")
    public WebResponse postStatus(@WebUser User user,@WebParam("status")String status, RequestContext rc) throws Exception {
    	String timeline = twitterService.postStatus(user,status);
    	WebResponse response = WebResponse.success(timeline);
    	return response;
    }
    
    @WebPost("/twitter/retweet")
    public WebResponse retweet(@WebUser User user,@WebParam("tweet_id")String tweet_id, RequestContext rc) throws Exception {
    	Map retweet = twitterService.retweet(user,tweet_id);
    	WebResponse response = WebResponse.success(retweet);
    	return response;
    }
    
    @WebGet("/twitter/getRetweetById")
    public WebResponse getRetweetById(@WebUser User user,@WebParam("tweet_id")String tweet_id) {
    	System.out.println("tweet_id:" + tweet_id);
    	String retweet = twitterService.getRetweetById(user,tweet_id);
    	WebResponse response = WebResponse.success(retweet);
    	return response;
    }
    
    @WebPost("/twitter/destroyTweet")
    public WebResponse destroyTweet(@WebUser User user,@WebParam("tweet_id")String tweet_id, RequestContext rc) throws Exception {
    	Map destroyTweet = twitterService.destroyTweet(user,tweet_id);
    	WebResponse response = WebResponse.success(destroyTweet);
    	return response;
    }
    
    @WebGet("/twitter/getSuggestions")
    public WebResponse getSuggestions(@WebUser User user, RequestContext rc) throws Exception {
    	String suggestions = twitterService.getSuggestions(user);
    	WebResponse response = WebResponse.success(suggestions);
    	return response;
    }
    
    
    @WebPost("/twitter/favorite")
    public WebResponse favorite(@WebUser User user,@WebParam("tweet_id")String tweet_id, RequestContext rc) throws Exception {
    	Map retweet = twitterService.favorite(user,tweet_id);
    	WebResponse response = WebResponse.success(retweet);
    	return response;
    }

}
