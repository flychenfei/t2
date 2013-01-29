package com.britesnow.samplesocial.web;

import java.io.IOException;

import com.britesnow.samplesocial.service.YaoGithubAuthService;
import com.britesnow.snow.web.RequestContext;
import com.britesnow.snow.web.rest.annotation.WebGet;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class GitHubHandlers {

	@Inject
	private YaoGithubAuthService yaoGithubAuthService;
	
	@WebGet("/github/connects")
	public void auth(RequestContext rc) throws IOException{
		  rc.getRes().sendRedirect(yaoGithubAuthService.getAuthorizationUrl());
	}
}
