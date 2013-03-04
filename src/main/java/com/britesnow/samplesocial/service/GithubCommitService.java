package com.britesnow.samplesocial.service;

import java.io.IOException;
import java.util.List;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryCommit;
import org.eclipse.egit.github.core.RepositoryCommitCompare;
import org.eclipse.egit.github.core.service.CommitService;

import com.britesnow.samplesocial.entity.User;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class GithubCommitService {
	@Inject
	private YaoGithubAuthService githubAuthService;
	
	public List<RepositoryCommit> getCommits(Repository repository,User user) throws IOException{
		CommitService commitService = new CommitService(githubAuthService.createClient(user));
		return commitService.getCommits(repository);
	}
	
	public RepositoryCommit getCommit(Repository repository,User user,String sha) throws IOException{
		CommitService commitService = new CommitService(githubAuthService.createClient(user));
		return commitService.getCommit(repository, sha);
	}
	
	public RepositoryCommitCompare compareCommits(Repository repository,User user,String base,String head) throws IOException{
		CommitService commitService = new CommitService(githubAuthService.createClient(user));
		return commitService.compare(repository, base, head);
	}
}
