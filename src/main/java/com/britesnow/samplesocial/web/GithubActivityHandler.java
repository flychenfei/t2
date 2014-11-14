package com.britesnow.samplesocial.web;

import com.britesnow.samplesocial.entity.User;
import com.britesnow.samplesocial.service.GithubActivityService;
import com.britesnow.snow.web.param.annotation.WebUser;
import com.britesnow.snow.web.rest.annotation.WebGet;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class GithubActivityHandler {

	@Inject
	private GithubActivityService githubActivityService;
	
	@WebGet("/github/events")
	public WebResponse getPublicEvets(@WebUser User user){
		return WebResponse.success(githubActivityService.getPublicEvents(user));
	}
	
}
