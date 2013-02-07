package com.britesnow.samplesocial.service;

import java.io.IOException;
import java.util.List;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.service.RepositoryService;

import com.britesnow.samplesocial.entity.User;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class GithubRepositoriesService {

	@Inject
	private YaoGithubAuthService githubAuthService;
	
	public List<Repository> getRepositories(User user) throws IOException {
		RepositoryService repositoryService = new RepositoryService(githubAuthService.createClient(user));
	    return repositoryService.getRepositories();
	}
	
	public Repository createRepository(User user,Repository repo) throws IOException {
		RepositoryService repositoryService = new RepositoryService(githubAuthService.createClient(user));
	    return repositoryService.createRepository(repo);
	}
	
	public Repository editRepository(User user,Repository repo) throws IOException {
		RepositoryService repositoryService = new RepositoryService(githubAuthService.createClient(user));
	    return repositoryService.editRepository(repo);
	}
}
