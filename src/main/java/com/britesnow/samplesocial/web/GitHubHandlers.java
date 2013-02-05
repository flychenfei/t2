package com.britesnow.samplesocial.web;

import java.io.IOException;

import com.britesnow.samplesocial.entity.User;
import com.britesnow.samplesocial.service.GithubService;
import com.britesnow.samplesocial.service.YaoGithubAuthService;
import com.britesnow.snow.web.RequestContext;
import com.britesnow.snow.web.param.annotation.WebParam;
import com.britesnow.snow.web.param.annotation.WebUser;
import com.britesnow.snow.web.rest.annotation.WebGet;
import com.britesnow.snow.web.rest.annotation.WebPost;
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
		return WebResponse.success(userInfo).set("emails", githubService.getEmails(user));
	}
	
	@WebGet("/github/repositories")
	public WebResponse getRepositories(RequestContext rc,@WebUser User user) throws IOException{
		String userInfo = githubService.getRepositories(user);
		return WebResponse.success(userInfo);
	}
	
	@WebPost("/github/addEmail")
	public WebResponse addEmail(@WebParam("email") String email,@WebUser User user) throws IOException{
		String status = githubService.addEmail(email, user);
		return WebResponse.success(status).set("email", email).set("addSuccess", status.startsWith("adding"));
	}
	
	@WebPost("/github/deleteEmail")
	public WebResponse deleteEmail(@WebParam("email") String email,@WebUser User user) throws IOException{
		String status = githubService.deleteEmail(email, user);
		return WebResponse.success(status).set("deleteSuccess", status.contains("successfully"));
	}
}
