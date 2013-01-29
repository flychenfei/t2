package com.britesnow.samplesocial.web;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.britesnow.samplesocial.dao.SocialIdEntityDao;
import com.britesnow.samplesocial.entity.SocialIdEntity;
import com.britesnow.samplesocial.entity.User;
import com.britesnow.samplesocial.oauth.ServiceType;
import com.britesnow.samplesocial.service.FacebookAuthService;
import com.britesnow.samplesocial.service.GithubAuthService;
import com.britesnow.samplesocial.service.GoogleAuthService;
import com.britesnow.samplesocial.service.LinkedInAuthService;
import com.britesnow.samplesocial.service.SalesForceAuthService;
import com.britesnow.samplesocial.service.TwitterAuthService;
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
    private SocialIdEntityDao   socialIdEntityDao;

    @WebGet("/authorize")
    public void authorize(@WebModel Map m,@WebParam("service") ServiceType service, RequestContext rc) throws IOException {
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
        }
        rc.getRes().sendRedirect(url);
    }

    @WebModelHandler(startsWith="/callback_fb")
    public void fbCallback(@WebModel Map m, @WebUser User user,@WebParam("code") String code,  RequestContext rc) {
        String[] tokens = facebookAuthService.getAccessToken(code);
        System.out.println("--->" + tokens[0]);
        SocialIdEntity s =   facebookAuthService.getSocialIdEntity(user.getId());
        String[] strArr =tokens[2].split("&expires=");
        String expire = strArr[1];
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.SECOND,new Integer(expire)/1000);
        Date tokenDate = cal.getTime();
        if (s==null) {
            s = new SocialIdEntity();
            s.setUser_id(user.getId());
            s.setToken(tokens[0]);
            s.setService(ServiceType.FaceBook);
            s.setTokenDate(tokenDate);
            socialIdEntityDao.save(s);
        }else{
            s.setTokenDate(tokenDate);
            s.setToken(tokens[0]);
            socialIdEntityDao.update(s);
        }
    }

    @WebModelHandler(startsWith="/linkedinCallback")
    public void linkedinCallback(RequestContext rc,@WebUser User user,  @WebParam("oauth_token") String reqToken,
                                 @WebParam("oauth_verifier") String code) throws Exception {
        if (user!=null && code != null) {
            linkedInAuthService.updateAccessToken(reqToken, code, user.getId());
        }else{
            rc.getRes().sendRedirect(linkedInAuthService.getAuthorizationUrl());
        }
    }


    @WebModelHandler(startsWith="/github_callback")
    public void githubCallback(RequestContext rc,@WebUser User user,  @WebParam("code") String code) throws Exception {
        System.out.println(code);
        if (user!=null && code != null) {
            githubAuthService.updateAccessToken(code, user.getId());
        }else{
            rc.getRes().sendRedirect(githubAuthService.getAuthorizationUrl());
        }
    }

    @WebModelHandler(startsWith="/googleCallback")
    public void googleCallback(@WebUser User user, RequestContext rc, @WebParam("code") String code) throws Exception {
        if (user != null && code != null) {
            googleAuthService.updateAccessToken(code, user.getId());
        } else {
            rc.getRes().sendRedirect(googleAuthService.getAuthorizationUrl());
        }

    }


    @WebModelHandler(startsWith="/salesforce_callback")
    public void salesforceCallback(RequestContext rc, @WebUser User user,@WebParam("code") String code) throws Exception {
        String[] tokens = salesForceAuthService.getAccessToken(code);
        SocialIdEntity s =   salesForceAuthService.getSocialIdEntity(user.getId());
        Pattern expirePattern = Pattern.compile("\"issued_at\":\\s*\"(\\S*?)\"");
        Matcher matcher = expirePattern.matcher(tokens[2]);
        String expire = null;
        if(matcher.find()){
            expire = matcher.group(1);
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.SECOND,new Long(expire).intValue()/1000);
        Date tokenDate = cal.getTime();
        if (s==null) {
            s = new SocialIdEntity();
            s.setUser_id(user.getId());
            s.setToken(tokens[0]);
            s.setService(ServiceType.SalesForce);
            s.setTokenDate(tokenDate);
            socialIdEntityDao.save(s);
        }else{
            s.setToken(tokens[0]);
            s.setTokenDate(tokenDate);
            socialIdEntityDao.update(s);
        }
    }

}
