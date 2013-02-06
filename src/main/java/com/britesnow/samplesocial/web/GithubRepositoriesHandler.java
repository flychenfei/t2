package com.britesnow.samplesocial.web;

import java.io.IOException;

import org.eclipse.egit.github.core.Repository;

import com.britesnow.samplesocial.entity.User;
import com.britesnow.samplesocial.service.GithubRepositoriesService;
import com.britesnow.snow.web.RequestContext;
import com.britesnow.snow.web.param.annotation.WebParam;
import com.britesnow.snow.web.param.annotation.WebUser;
import com.britesnow.snow.web.rest.annotation.WebGet;
import com.britesnow.snow.web.rest.annotation.WebPost;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class GithubRepositoriesHandler {

	@Inject
	private GithubRepositoriesService githubRepositoriesService;
	
	@WebGet("/github/repositories")
	public WebResponse getRepositories(RequestContext rc,@WebUser User user) throws IOException{
		return WebResponse.success(githubRepositoriesService.getRepositories(user));
	}
	
	@WebPost("/github/createRepository")
	public WebResponse createRepository(@WebUser User user,@WebParam("name") String name,
			@WebParam("description") String description) {
		Repository repo = new Repository();
		repo.setName(name);
		repo.setDescription(description);
		try{
			repo = githubRepositoriesService.createRepository(user, repo);
			return WebResponse.success(repo);
		}catch(Exception e){
			return WebResponse.fail(e.getMessage());
		}
		
	}
	
}
