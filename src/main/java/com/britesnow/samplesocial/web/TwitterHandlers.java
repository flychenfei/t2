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
    
    @WebPost("/twitter/favorite")
    public WebResponse favorite(@WebUser User user,@WebParam("tweet_id")String tweet_id, RequestContext rc) throws Exception {
    	Map retweet = twitterService.favorite(user,tweet_id);
    	WebResponse response = WebResponse.success(retweet);
    	return response;
    }

}
