package com.britesnow.samplesocial.web;

import java.io.IOException;

import org.apache.commons.fileupload.FileItem;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.Repository;

import com.britesnow.samplesocial.entity.User;
import com.britesnow.samplesocial.service.GithubCommitService;
import com.britesnow.samplesocial.service.GithubRepositoriesService;
import com.britesnow.samplesocial.service.GithubUserService;
import com.britesnow.snow.util.JsonUtil;
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
	@Inject
	private GithubCommitService githubCommitService;
	@Inject
	private GithubUserService githubUserService;
	
	/**
	 * get all repositories for user
	 * @param rc
	 * @param user
	 * @return
	 * @throws IOException
	 */
	@WebGet("/github/repositories")
	public WebResponse getRepositories(RequestContext rc,@WebUser User user) throws IOException{
		return WebResponse.success(githubRepositoriesService.getRepositories(user));
	}
	
	/**
	 * create a new repository
	 * @param user web user
	 * @param name the name for repository
	 * @param description the description for repository
	 * @return
	 */
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
	
	/**
	 * edit repository for description
	 * @param user
	 * @param name  name of repository
	 * @param description
	 * @param repositoryId  id of repository 
	 * @param login current github user login name
	 * @return
	 * @throws IOException
	 */
	@WebPost("/github/editRepository")
	public WebResponse editRepository(@WebUser User user,@WebParam("name") String name,
			@WebParam("description") String description,@WebParam("repositoryId")Long repositoryId,
			@WebParam("login") String login) throws IOException{
		//since the repository edit must need the login and name to generateId,so need these parameters
		Repository repo = new Repository();
		repo.setDescription(description);
		repo.setOwner(githubUserService.getGithubUser(user));
		repo.setName(name);
		repo.setId(repositoryId);
		try{
			repo = githubRepositoriesService.editRepository(user, repo);
			return WebResponse.success(repo);
		}catch(Exception e){
			return WebResponse.fail(e.getMessage());
		}
	}
	
	/**
	 * get commits for a repository
	 * @param user
	 * @param name name of repository
	 * @param login current github user login name
	 * @return
	 * @throws IOException
	 */
	@WebGet("/github/getCommits")
	public WebResponse getCommits(@WebUser User user,@WebParam("name") String name,
			@WebParam("login") String login) throws IOException {
		//since the repository edit must need the login and name to generateId,so need these parameters
		Repository repo = new Repository();
		org.eclipse.egit.github.core.User owner = githubUserService.getGithubUser(user);
		owner.setLogin(login);
		repo.setOwner(owner);
		repo.setName(name);
		try{
			return WebResponse.success(githubCommitService.getCommits(repo, user));
		}catch(Exception e){
			return WebResponse.fail(e.getMessage());
		}
	}
	
	/**
	 * get single commit 
	 * @param user
	 * @param name
	 * @param login
	 * @param sha the sha for single commit
	 * @return
	 * @throws IOException
	 */
	@WebGet("/github/getCommit")
	public WebResponse getCommit(@WebUser User user,@WebParam("name") String name,
			@WebParam("login") String login,@WebParam("sha") String sha) throws IOException {
		Repository repo = new Repository();
		repo.setOwner(githubUserService.getGithubUser(user));
		repo.setName(name);
		try{
			return WebResponse.success(githubCommitService.getCommit(repo, user, sha));
		}catch(Exception e){
			return WebResponse.fail(e.getMessage());
		}
	}
	
	/**
	 * compare two commits
	 * @param user
	 * @param name
	 * @param base sha for base commit
	 * @param head sha for head commit
	 * @return
	 */
	@WebGet("/github/compareCommits")
	public WebResponse compareCommits(@WebUser User user,@WebParam("name") String name,
			@WebParam("base") String base,@WebParam("head") String head){
		try{
		Repository repo = new Repository();
		repo.setOwner(githubUserService.getGithubUser(user));
		repo.setName(name);
		return WebResponse.success(githubCommitService.compareCommits(repo, user, base, head));
		}catch(Exception e){
			return WebResponse.fail(e.getMessage());
		}
	}
	
	/**
	 * get readme content of repository
	 * @param user
	 * @param repo name of repository
	 * @return
	 * @throws IOException
	 */
	@WebGet("/github/getReadme")
	public WebResponse getReadme(@WebUser User user,@WebParam("repo") String repo) throws IOException{
		return WebResponse.success(githubRepositoriesService.getReadme(user, repo))
				.set("archiveLink", githubRepositoriesService.getArchiveLink(user, repo, "zipball", "master"));
		
	}
	
	/**
	 * get content of file or folder with given path
	 * @param user
	 * @param repo name of repository
	 * @param path path of file or folder 
	 * @return
	 * @throws IOException
	 */
	@WebGet("/github/getContents")
	public WebResponse getContents(@WebUser User user,@WebParam("repo") String repo,@WebParam("path")String path) throws IOException{
		return WebResponse.success(githubRepositoriesService.getContents(user, repo, path));
	}
	
	/**
	 * List downloads for a repository
	 * @param user
	 * @param repo name of repository
	 * @return
	 * @throws IOException
	 */
	@WebGet("/github/getDownloads")
	public WebResponse getDownloads(@WebUser User user,@WebParam("repo") String repo) throws IOException{
		Repository repository = new Repository();
		repository.setOwner(githubUserService.getGithubUser(user));
		repository.setName(repo);
		return WebResponse.success(githubRepositoriesService.getDownloads(user, repository));
	}
	
	/**
	 * Create a download
	 * @param user
	 * @param data
	 * @param item
	 * @return
	 * @throws IOException
	 */
	@WebPost("/github/createDownload")
	public WebResponse createDownload(@WebUser User user,@WebParam("data") String data,
			@WebParam("file") FileItem item) throws IOException{
		String repo = JsonUtil.toMapAndList(data).get("repo").toString();
		System.out.println(repo);
		Repository repository = new Repository();
		repository.setOwner(githubUserService.getGithubUser(user));
		repository.setName(repo);
		System.out.println(repository.generateId());
		return WebResponse.success(githubRepositoriesService.createDownload(user, repository, item));
	}
	
	/**
	 * Delete a download
	 * @param user
	 * @param repoId
	 * @return
	 * @throws IOException
	 */
	@WebPost("/github/deleteDownload")
	public WebResponse deleteDownload(@WebUser User user,@WebParam("repo") String repo,
			@WebParam("repoId") String repoId) throws IOException{
		Repository repository = new Repository();
		repository.setOwner(githubUserService.getGithubUser(user));
		repository.setName(repo);
		githubRepositoriesService.deleteDownload(user, repository, Integer.parseInt(repoId));
		return WebResponse.success();
	}
	
	/**
	 * List forks
	 * @param user
	 * @param repo
	 * @return
	 * @throws IOException
	 */
	@WebGet("/github/getForks")
	public WebResponse getForks(@WebUser User user,@WebParam("repo") String repo) throws IOException{
		Repository repository = new Repository();
		repository.setOwner(githubUserService.getGithubUser(user));
		repository.setName(repo);
		githubRepositoriesService.CreateFork(user, repository);
		return WebResponse.success(githubRepositoriesService.getForks(user, repository));
	}
	
	/**
	 * Create a fork
	 * @param user
	 * @param repo
	 * @return
	 * @throws IOException
	 */
	@WebPost("/github/createFork")
	public WebResponse createFork(@WebUser User user,@WebParam("login") String login,
			@WebParam("repo")String repo) throws IOException{
		Repository repository = new Repository();
		org.eclipse.egit.github.core.User githubUser = new org.eclipse.egit.github.core.User();
		githubUser.setLogin(login);
		repository.setOwner(githubUser);
		repository.setName(repo);
		githubRepositoriesService.CreateFork(user, repository);
		return WebResponse.success(githubRepositoriesService.getForks(user, repository));
	}

	/**
	 * get Issues for a repository
	 * @param user
	 * @param name name of repository
	 * @param login current github user login name
	 * @return
	 * @throws IOException
	 */
	@WebGet("/github/getIssues")
	public WebResponse getIssues(@WebUser User user,@WebParam("name") String name,
								  @WebParam("login") String login,@WebParam("state") String state) throws IOException {
		Repository repo = new Repository();
		org.eclipse.egit.github.core.User owner = githubUserService.getGithubUser(user);
		owner.setLogin(login);
		repo.setOwner(owner);
		repo.setName(name);
		try{
			return WebResponse.success(githubRepositoriesService.getIssues(repo, user,state));
		}catch(Exception e){
			return WebResponse.fail(e.getMessage());
		}
	}

	/**
	 * get Issue for a repository
	 * @param user
	 * @param name name of repository
	 * @param login current github user login name
	 * @return
	 * @throws IOException
	 */
	@WebGet("/github/getIssue")
	public WebResponse getIssue(@WebUser User user,@WebParam("name") String name,
								 @WebParam("login") String login,@WebParam("issueNumber") String issueNumber) throws IOException {
		Repository repo = new Repository();
		org.eclipse.egit.github.core.User owner = githubUserService.getGithubUser(user);
		owner.setLogin(login);
		repo.setOwner(owner);
		repo.setName(name);
		try{

			return WebResponse.success(githubRepositoriesService.getIssue(repo, user, issueNumber));
		}catch(Exception e){
			return WebResponse.fail(e.getMessage());
		}
	}

	/**
	 * new Issue for a repository
	 * @param user
	 * @param name name of repository
	 * @param login current github user login name
	 * @param title title of issue
	 * @param body body of issue
	 * @return
	 * @throws IOException
	 */
	@WebGet("/github/newIssue")
	public WebResponse newIssue(@WebUser User user,@WebParam("name") String name,
								@WebParam("login") String login,@WebParam("title") String title,
								@WebParam("body") String body) throws IOException {
		Repository repo = new Repository();
		org.eclipse.egit.github.core.User owner = githubUserService.getGithubUser(user);
		owner.setLogin(login);
		repo.setOwner(owner);
		repo.setName(name);
		try{
			Issue issue = new Issue();
			issue.setTitle(title);
			issue.setBody(body);
			issue.setAssignee(owner);
			return WebResponse.success(githubRepositoriesService.newIssue(repo, user, issue));
		}catch(Exception e){
			return WebResponse.fail(e.getMessage());
		}
	}

	/**
	* get Releases for a repository
	* @param user
	* @param name name of repository
	* @param login current github user login name
	* @return
			* @throws IOException
	*/
	@WebGet("/github/getReleases")
	public WebResponse getReleases(@WebUser User user,@WebParam("name") String name,
								   @WebParam("login") String login) throws IOException {
		Repository repo = new Repository();
		org.eclipse.egit.github.core.User owner = githubUserService.getGithubUser(user);
		owner.setLogin(login);
		repo.setOwner(owner);
		repo.setName(name);
		return WebResponse.success(githubRepositoriesService.getReleases(repo, user));
	}
}
