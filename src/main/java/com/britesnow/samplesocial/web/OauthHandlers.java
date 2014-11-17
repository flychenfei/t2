package com.britesnow.samplesocial.web;

import java.io.IOException;
import java.util.Map;

import org.scribe.model.Token;

import com.britesnow.samplesocial.entity.User;
import com.britesnow.samplesocial.feed.FeedGmailAnalyticsManager;
import com.britesnow.samplesocial.oauth.OauthException;
import com.britesnow.samplesocial.oauth.ServiceType;
import com.britesnow.samplesocial.service.DropboxAuthService;
import com.britesnow.samplesocial.service.FacebookAuthService;
import com.britesnow.samplesocial.service.FoursquareAuthService;
import com.britesnow.samplesocial.service.GithubAuthService;
import com.britesnow.samplesocial.service.GoogleAuthService;
import com.britesnow.samplesocial.service.LinkedInAuthService;
import com.britesnow.samplesocial.service.LiveAuthService;
import com.britesnow.samplesocial.service.SalesForceAuthService;
import com.britesnow.samplesocial.service.TwitterAuthService;
import com.britesnow.samplesocial.service.YahooAuthService;
import com.britesnow.snow.web.RequestContext;
import com.britesnow.snow.web.handler.annotation.WebModelHandler;
import com.britesnow.snow.web.param.annotation.WebModel;
import com.britesnow.snow.web.param.annotation.WebParam;
import com.britesnow.snow.web.param.annotation.WebUser;
import com.britesnow.snow.web.rest.annotation.WebGet;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class OauthHandlers {
    @Inject
    private FacebookAuthService facebookAuthService;
    @Inject
    private GoogleAuthService googleAuthService;
    @Inject
    private LinkedInAuthService linkedInAuthService;
    @Inject
    private SalesForceAuthService salesForceAuthService;
    @Inject
    private GithubAuthService githubAuthService;
    @Inject
    private TwitterAuthService twitterAuthService;
    @Inject
    private LiveAuthService liveAuthService;
    @Inject
    private FoursquareAuthService foursquareAuthService;
    @Inject
    private DropboxAuthService dropboxAuthService;
    @Inject
    private YahooAuthService yahooAuthService;
    @Inject
    private FeedGmailAnalyticsManager   feedGmailAnalyticsManager;

    @WebGet("/authorize")
    public void authorize(@WebModel Map<?, ?> m,@WebParam("service") ServiceType service, RequestContext rc) throws IOException {
        String url = "";
        if (service == ServiceType.FaceBook) {
            url = facebookAuthService.getAuthorizationUrl();
        }else if(service == ServiceType.Google){
            url = googleAuthService.getAuthorizationUrl();
        }else if(service == ServiceType.LinkedIn){
            url = linkedInAuthService.getAuthorizationUrl();
        }else if(service == ServiceType.SalesForce){
            url = salesForceAuthService.getAuthorizationUrl();
        }else if (service == ServiceType.Github) {
            url = githubAuthService.getAuthorizationUrl();
        }else if (service == ServiceType.Twitter) {
            url = twitterAuthService.getAuthorizationUrl();
        }else if (service == ServiceType.Live) {
            url = liveAuthService.getAuthorizationUrl();
        }else if (service == ServiceType.Foursquare) {
            url = foursquareAuthService.getAuthorizationUrl();
        }else if (service == ServiceType.Dropbox) {
            url = dropboxAuthService.getAuthorizationUrl();
        }else if (service == ServiceType.Yahoo) {
            url = yahooAuthService.getAuthorizationUrl();
        }
        
        rc.getRes().sendRedirect(url);
    }

    @WebModelHandler(startsWith="/callback_fb")
    public void fbCallback(@WebModel Map<?, ?> m, @WebUser User user,@WebParam("code") String code,  RequestContext rc) {
        facebookAuthService.updateAccessToken(code);
    }
    
    @WebModelHandler(startsWith="/twitterCallback")
    public void twitterCallback(@WebUser User user, @WebParam("oauth_token") String requestToken, 
    		@WebParam("oauth_verifier") String verifier,  RequestContext rc) throws Exception {
    	 if (user != null && verifier != null) {
             twitterAuthService.updateAccessToken(requestToken, verifier);
         }else{
			rc.getRes().sendRedirect(twitterAuthService.getAuthorizationUrl());
         }
    }
    
    @WebModelHandler(startsWith="/linkedinCallback")
    public void linkedinCallback(RequestContext rc,@WebUser User user,  @WebParam("oauth_token") String reqToken,
                                 @WebParam("oauth_verifier") String code) throws Exception {
        if (user!=null && code != null) {
            linkedInAuthService.updateAccessToken(reqToken, code);
        }else{
            rc.getRes().sendRedirect(linkedInAuthService.getAuthorizationUrl());
        }
    }
    @WebModelHandler(startsWith="/liveCallback")
    public void liveCallback(RequestContext rc,@WebUser User user, @WebParam("code") String code) throws Exception {
        if (user!=null && code != null) {
            liveAuthService.updateAccessToken(code);
        }else{
            rc.getRes().sendRedirect(liveAuthService.getAuthorizationUrl());
        }
    }

    @WebModelHandler(startsWith="/foursquareCallback")
    public void fourquareCallback(RequestContext rc,@WebUser User user, @WebParam("code") String code) throws Exception {
        if (user!=null && code != null) {
            foursquareAuthService.updateAccessToken(code);
        }else{
            rc.getRes().sendRedirect(foursquareAuthService.getAuthorizationUrl());
        }
    }

    @WebModelHandler(startsWith="/github_callback")
    public void githubCallback(RequestContext rc,@WebUser User user,  @WebParam("code") String code) throws Exception {
    	if (user!=null && code != null) {
            githubAuthService.updateAccessToken(code);
        }else{
            rc.getRes().sendRedirect(githubAuthService.getAuthorizationUrl());
        }
    }

    @WebModelHandler(startsWith="/googleCallback")
    public void googleCallback(@WebUser User user, RequestContext rc, @WebParam("code") String code) throws Exception {
    	if (user != null && code != null) {
            user.setGoogle_access_token(googleAuthService.updateAccessToken(code));
            feedGmailAnalyticsManager.addTask(user);
        } else {
            rc.getRes().sendRedirect(googleAuthService.getAuthorizationUrl());
        }
    }

    @WebModelHandler(startsWith="/dropbox_callback")
    public void dropboxCallback(@WebUser User user, RequestContext rc, @WebParam("oauth_token") String oauth_token,
    		@WebParam("not_approved") Boolean not_approved)  {
    	if(not_approved==null||!not_approved){
	      Token authToken = dropboxAuthService.getTokenByAuthToken(oauth_token);
	      dropboxAuthService.updateAccessToken(authToken,user);
    	}else
    	 throw new OauthException(dropboxAuthService.getAuthorizationUrl());
    }

    @WebModelHandler(startsWith="/salesforce_callback")
    public void salesforceCallback(RequestContext rc, @WebUser User user,@WebParam("code") String code) {
        salesForceAuthService.updateAccessToken(code);
    }

    @WebModelHandler(startsWith="/yahoo_callback")
    public void yahooCallback(RequestContext rc,@WebUser User user,  @WebParam("oauth_token") String reqToken,
                                 @WebParam("oauth_verifier") String code) throws Exception {
        if (user!=null && code != null) {
            yahooAuthService.updateAccessToken(reqToken, code);
        }else{
            rc.getRes().sendRedirect(yahooAuthService.getAuthorizationUrl());
        }
    }

}
