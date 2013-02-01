package com.britesnow.samplesocial.web;

import java.io.IOException;

import com.britesnow.samplesocial.entity.User;
import com.britesnow.samplesocial.service.GithubService;
import com.britesnow.samplesocial.service.YaoGithubAuthService;
import com.britesnow.snow.web.RequestContext;
import com.britesnow.snow.web.param.annotation.WebUser;
import com.britesnow.snow.web.rest.annotation.WebGet;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class GitHubHandlers {

	@Inject
	private YaoGithubAuthService yaoGithubAuthService;
	
	
	@Inject
	private GithubService githubService;
	@WebGet("/github/connects")
	public void auth(RequestContext rc) throws IOException{
		  rc.getRes().sendRedirect(yaoGithubAuthService.getAuthorizationUrl());
	}
	
	
	@WebGet("/github/userInfo")
	public WebResponse getUserInfo(RequestContext rc,@WebUser User user) throws IOException{
		String userInfo = githubService.getUserInfo(user);
		githubService.addEmail("swbyzx@126.com", user);
		return WebResponse.success(userInfo).set("emails", githubService.getEmails(user));
	}
	
	@WebGet("/github/repositories")
	public WebResponse getRepositories(RequestContext rc,@WebUser User user) throws IOException{
		String userInfo = githubService.getRepositories(user);
		return WebResponse.success(userInfo);
	}
}
