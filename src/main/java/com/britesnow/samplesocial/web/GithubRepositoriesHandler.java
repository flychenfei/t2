package com.britesnow.samplesocial.web;

import java.io.IOException;

import com.britesnow.samplesocial.entity.User;
import com.britesnow.samplesocial.service.GithubUserService;
import com.britesnow.snow.web.RequestContext;
import com.britesnow.snow.web.param.annotation.WebUser;
import com.britesnow.snow.web.rest.annotation.WebGet;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class GithubRepositoriesHandler {

	@Inject
	private GithubUserService githubUserService;
	
	@WebGet("/github/repositories")
	public WebResponse getRepositories(RequestContext rc,@WebUser User user) throws IOException{
		String userInfo = githubUserService.getRepositories(user);
		return WebResponse.success(userInfo);
	}
	
}
