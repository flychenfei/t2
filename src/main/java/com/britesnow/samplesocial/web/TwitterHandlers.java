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
    public WebResponse getUserInfo(@WebUser User user, RequestContext rc)  {
    	Map userInfo = twitterService.getUserInfo(user);
    	WebResponse response = WebResponse.success(userInfo);
    	return response;
    }
    
    @WebGet("/twitter/getTimeline")
    public WebResponse getTimeline(@WebUser User user, RequestContext rc) {
    	String timeline = twitterService.getTimeline(user);
    	WebResponse response = WebResponse.success(timeline);
    	return response;
    }
    
    
    @WebGet("/twitter/getRetweets")
    public WebResponse getRetweets(@WebUser User user, RequestContext rc)  {
    	String retweets = twitterService.getRetweets(user);
    	WebResponse response = WebResponse.success(retweets);
    	return response;
    }
    
    @WebGet("/twitter/getMentionTimeline")
    public WebResponse getMentionTimeline(@WebUser User user, RequestContext rc)  {
    	String timeline = twitterService.getMentionTimeline(user);
    	WebResponse response = WebResponse.success(timeline);
    	return response;
    }
    
    @WebGet("/twitter/getUserTimeline")
    public WebResponse getUserTimeline(@WebUser User user, RequestContext rc)  {
    	String timeline = twitterService.getUserTimeline(user);
    	WebResponse response = WebResponse.success(timeline);
    	return response;
    }
    
    @WebPost("/twitter/postStatus")
    public WebResponse postStatus(@WebUser User user,@WebParam("status")String status, RequestContext rc)  {
    	String timeline = twitterService.postStatus(user,status);
    	WebResponse response = WebResponse.success(timeline);
    	return response;
    }
    
    @WebPost("/twitter/retweet")
    public WebResponse retweet(@WebUser User user,@WebParam("tweet_id")String tweet_id, RequestContext rc)  {
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
    
    @WebGet("/twitter/getStatusById")
    public WebResponse getStatusById(@WebUser User user,@WebParam("status_id")String status_id) {
    	System.out.println("status_id:" + status_id);
    	Map status = twitterService.getStatusById(user,status_id);
    	WebResponse response = WebResponse.success(status);
    	return response;
    }
    
    @WebPost("/twitter/destroyTweet")
    public WebResponse destroyTweet(@WebUser User user,@WebParam("tweet_id")String tweet_id, RequestContext rc)  {
    	Map destroyTweet = twitterService.destroyTweet(user,tweet_id);
    	WebResponse response = WebResponse.success(destroyTweet);
    	return response;
    }
    
    @WebGet("/twitter/searchTweets")
    public WebResponse searchTweets(@WebUser User user, @WebParam("query")String query) {
    	System.out.println("query:" + query);
    	Map tweets = twitterService.searchTweets(user, query);
    	WebResponse response = WebResponse.success(tweets);
    	return response;
    }
    
    @WebGet("/twitter/getDirectMsg")
    public WebResponse getSampleStatus(@WebUser User user) {
    	String tweets = twitterService.getDirectMsg(user);
    	WebResponse response = WebResponse.success(tweets);
    	return response;
    }
    
    @WebGet("/twitter/getSendMsg")
    public WebResponse showMsg(@WebUser User user) {
    	String tweets = twitterService.getSendMsg(user);
    	WebResponse response = WebResponse.success(tweets);
    	return response;
    }
    
    @WebGet("/twitter/showMsg")
    public WebResponse showMsg(@WebUser User user, @WebParam("msg_id")String msg_id) {
    	System.out.println("msg_id:" + msg_id);
    	String tweets = twitterService.showMsg(user, msg_id);
    	WebResponse response = WebResponse.success(tweets);
    	return response;
    }
    
    @WebPost("/twitter/sendMsg")
    public WebResponse sendMsg(@WebUser User user, @WebParam("screen_name")String screen_name, 
    		@WebParam("text")String text) {
    	System.out.println("screen_name:" + screen_name);
    	System.out.println("text:" + text);
    	Map message = twitterService.sendMsg(user, screen_name, text);
    	WebResponse response = WebResponse.success(message);
    	return response;
    }
    
    @WebGet("/twitter/getSuggestions")
    public WebResponse getSuggestions(@WebUser User user, RequestContext rc)  {
    	String suggestions = twitterService.getSuggestions(user);
    	WebResponse response = WebResponse.success(suggestions);
    	return response;
    }
    
    
    @WebPost("/twitter/favorite")
    public WebResponse favorite(@WebUser User user,@WebParam("tweet_id")String tweet_id, RequestContext rc)  {
    	Map retweet = twitterService.favorite(user,tweet_id);
    	WebResponse response = WebResponse.success(retweet);
    	return response;
    }

}
