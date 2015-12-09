package com.britesnow.samplesocial.service;

import com.britesnow.samplesocial.entity.GithubRelease;
import com.britesnow.samplesocial.entity.User;
import com.britesnow.snow.util.JsonUtil;
import com.britesnow.snow.util.MapUtil;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import javafx.util.Pair;
import org.apache.commons.fileupload.FileItem;
import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.Download;
import org.eclipse.egit.github.core.DownloadResource;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.PullRequest;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.SearchRepository;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.GitHubRequest;
import org.eclipse.egit.github.core.client.PageIterator;
import org.eclipse.egit.github.core.service.DownloadService;
import org.eclipse.egit.github.core.service.IssueService;
import org.eclipse.egit.github.core.service.PullRequestService;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.eclipse.egit.github.core.util.EncodingUtils;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Verb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Singleton
public class GithubRepositoriesService {
	
	private static String PREFIX = "https://api.github.com";
	
	@Inject
	private GithubAuthService githubAuthService;
	@Inject
	private GithubUserService githubUserService;
	
	/**
	 * get all repositories
	 * @param user
	 * @return
	 * @throws IOException
	 */
	public List<Repository> getRepositories(User user) throws IOException {
		RepositoryService repositoryService = new RepositoryService(githubAuthService.createClient(user));
	    return repositoryService.getRepositories();
	}
	
	/**
	 * create a new repository
	 * @param user
	 * @param repo
	 * @return
	 * @throws IOException
	 */
	public Repository createRepository(User user,Repository repo) throws IOException {
		RepositoryService repositoryService = new RepositoryService(githubAuthService.createClient(user));
	    return repositoryService.createRepository(repo);
	}
	
	/**
	 * edit repository for description
	 * @param user
	 * @param repo
	 * @return
	 * @throws IOException
	 */
	public Repository editRepository(User user,Repository repo) throws IOException {
		RepositoryService repositoryService = new RepositoryService(githubAuthService.createClient(user));
	    return repositoryService.editRepository(repo);
	}
	
	/**
	 * get content of readme for repository
	 * @param user
	 * @param repo
	 * @return
	 * @throws IOException
	 */
	public Map getReadme(User user,String repo) throws IOException{
		OAuthRequest request = githubAuthService.createRequest(Verb.GET,
				PREFIX+"/repos/"+githubUserService.getGithubUser(user).getLogin()+"/"+repo+
				"/readme"+"?access_token="+githubAuthService.getToken(user).getToken());
		Response response = request.send();
		Map m = JsonUtil.toMapAndList(response.getBody());
		String content = (String) m.get("content");
		if(content!=null)
			m.put("content", new String(EncodingUtils.fromBase64(content)));
		return m;
	}
	
	/**
	 * list files or get file content
	 * @param user
	 * @param repo
	 * @param path the path of file or folder
	 * @return
	 * @throws IOException
	 */
	public Object getContents(User user,String repo,String path) throws IOException{
		if(path==null)
			path="";
		if(path.startsWith("/"))
			path = "/"+path;
		OAuthRequest request = githubAuthService.createRequest(Verb.GET,
				PREFIX + "/repos/" + githubUserService.getGithubUser(user).getLogin() + "/" + repo +
						"/contents" + path + "?access_token=" + githubAuthService.getToken(user).getToken());
		Response response = request.send();
		String result = response.getBody();
		if(result.startsWith("["))
			return result;
		else{
			Map m = JsonUtil.toMapAndList(result);
			m.put("content", new String(EncodingUtils.fromBase64((String)m.get("content"))));
			return m;
		}
	}
	
	/**
	 * get archiveLink of a repository
	 * @param user
	 * @param repo
	 * @param archiveFormat
	 * @param ref
	 * @return
	 * @throws IOException
	 */
	public String getArchiveLink(User user,String repo,String archiveFormat,String ref) throws IOException{
		return 	PREFIX+"/repos/"+githubUserService.getGithubUser(user).getLogin()+"/"+repo+
				"/"+archiveFormat+"/"+ref+"?access_token="+githubAuthService.getToken(user).getToken();
	}
	
	/**
	 * List downloads for a repository
	 * @param user
	 * @param repo
	 * @return
	 * @throws IOException
	 */
	public List<Download> getDownloads(User user,Repository repo) throws IOException{
		DownloadService downloadService = new DownloadService(githubAuthService.createClient(user));
		return downloadService.getDownloads(repo);
	}
	
	/**
	 * create a download for repository
	 * @param user
	 * @param repo
	 * @return
	 * @throws IOException
	 */
	public DownloadResource createDownload(User user,Repository repo,FileItem item) throws IOException{
		DownloadService downloadService = new DownloadService(githubAuthService.createClient(user));
		Download download = new Download();
		download.setContentType("application/octet-stream");
		download.setName(item.getName());
		DownloadResource re = downloadService.createDownload(repo, download, item.getInputStream(),item.getSize());
		return re;
	}
	
	/**
	 * Delete a download
	 * @param user
	 * @param repo
	 * @param repoId
	 * @throws IOException
	 */
	public void deleteDownload(User user,Repository repo,int repoId) throws IOException{
		DownloadService downloadService = new DownloadService(githubAuthService.createClient(user));
		downloadService.deleteDownload(repo, repoId);
	}
	
	/**
	 * List forks
	 * @param user
	 * @param repo
	 * @return
	 * @throws IOException
	 */
	public List<Repository> getForks(User user,Repository repo) throws IOException{
		RepositoryService repositoryService = new RepositoryService(githubAuthService.createClient(user));
		return repositoryService.getForks(repo);
	}
	
	/**
	 * Create a fork
	 * @param user
	 * @param repo
	 * @return
	 * @throws IOException
	 */
	public Repository CreateFork(User user,Repository repo) throws IOException{
		RepositoryService repositoryService = new RepositoryService(githubAuthService.createClient(user));
		return repositoryService.forkRepository(repo);
	}
	
	/**
	 * Search repositories
	 * @param user
	 * @param query
	 * @param startPage
	 * @return
	 * @throws IOException
	 */
	public List<SearchRepository> searchRepositories(User user,String query,int startPage) throws IOException{
		RepositoryService repositoryService = new RepositoryService(githubAuthService.createClient(user));
		return repositoryService.searchRepositories(query, startPage);
	}

	/*
	 * Search Issues
	 * @param repo
	 * @param user
	 * @return
	 * @throws IOException
	 */

	public List<Issue> getIssues(Repository repo,User user,String state,int pageIndex,int pageSize) throws IOException{
		IssueService issueService = new IssueService(githubAuthService.createClient(user));
		Map<String,String> filterData = new HashMap<>();
		filterData.put("state",state);
		PageIterator<Issue> issues = issueService.pageIssues(repo,filterData,pageIndex+1,pageSize);
		List<Issue> issueList = new ArrayList<>(issues.next());
		return issueList;
	}

	/*
	 * Search Issue
	 * @param repo
	 * @param user
	 * @return
	 * @throws IOException
	 */

	public Map<?, ?> getIssueWithComment(Repository repo, User user, String issueNumber) throws IOException{
		IssueService issueService = new IssueService(githubAuthService.createClient(user));
		return MapUtil.mapIt("issue", issueService.getIssue(repo, issueNumber), "comment", issueService.getComments(repo, issueNumber));
	}

	/**
	 * Get issue
	 * @param repo repo
	 * @param user user
	 * @param issueNumber issue number
	 * @return the issue
	 * @throws IOException
	 */
	public Issue getIssue(Repository repo, User user, String issueNumber) throws IOException{
		IssueService issueService = new IssueService(githubAuthService.createClient(user));
		return issueService.getIssue(repo, issueNumber);
	}

	/*
	 * new Issue
	 * @param repo
	 * @param user
	 * @return
	 * @throws IOException
	 */
	public Issue newIssue(Repository repo, User user, Issue issue) throws IOException {
		IssueService issueService = new IssueService(githubAuthService.createClient(user));
		return issueService.createIssue(repo, issue);
	}

	public Map getReleases(Repository repo,User user) throws IOException{
		GithubReleaseService releaseService = new GithubReleaseService(githubAuthService.createClient(user));
		Map map = new HashMap<>();
		List<GithubRelease> releases = releaseService.getReleases(repo);

		map.put("releases",releases);
		return map;
	}

	public GithubRelease editRelease(User user, Repository repo, GithubRelease release) throws IOException {
		GithubReleaseService releaseService = new GithubReleaseService(githubAuthService.createClient(user));
		release = releaseService.editRelease(repo, release);
		return release;
	}

	public GithubRelease createRelease(User user, Repository repo, GithubRelease release) throws IOException {
		GithubReleaseService releaseService = new GithubReleaseService(githubAuthService.createClient(user));
		release = releaseService.createRelease(repo, release);
		return release;
	}

	public void deleteRelease(User user, Repository repo, String releaseId) throws IOException {
		GithubReleaseService releaseService = new GithubReleaseService(githubAuthService.createClient(user));
		releaseService.deleteRelease(repo, releaseId);
	}

	public Map getPullRequests(Repository repo, User user, String state) throws IOException {
		PullRequestService pullRequestService = new PullRequestService(githubAuthService.createClient(user));
		List<PullRequest> pullRequests = pullRequestService.getPullRequests(repo,"all");
		List<PullRequest> currentPullRequests = pullRequests.stream().filter(issue -> issue.getState().equals(state)).collect(Collectors.toList());
		int currentSize = currentPullRequests.size();
		int totalSize = pullRequests.size();
		return MapUtil.mapIt("pullRequests",currentPullRequests,"openCount","open".equals(state) ? currentSize : totalSize - currentSize,
				"closedCount","closed".equals(state) ? currentSize : totalSize - currentSize);
	}

	public PullRequest getPullRequest(Repository repo, User user, int id) throws IOException {
		PullRequestService pullRequestService = new PullRequestService(githubAuthService.createClient(user));
		return pullRequestService.getPullRequest(repo, id);
	}

	public PullRequest editPullRequest(Repository repo, User user, PullRequest pullRequest) throws IOException {
		PullRequestService pullRequestService = new PullRequestService(githubAuthService.createClient(user));
		return pullRequestService.editPullRequest(repo, pullRequest);
	}

	/*
	 * edit Issue
	 * @param repo
	 * @param user
	 * @param state
	 * @param number
	 * @return
	 * @throws IOException
	 */
	public Issue editIssue(Repository repo, User user, Issue issue) throws IOException {
		IssueService issueService = new IssueService(githubAuthService.createClient(user));
		return issueService.editIssue(repo, issue);
	}

	/*
	 * edit Comment
	 * @param repo
	 * @param user
	 * @param comment
	 * @return
	 * @throws IOException
	 */
	public Comment editComment(Repository repo, User user, Comment comment) throws IOException {
		IssueService issueService = new IssueService(githubAuthService.createClient(user));
		return issueService.editComment(repo, comment);
	}

	/*
	 * delete Comment
	 * @param repo
	 * @param user
	 * @param commentId
	 * @return
	 * @throws IOException
	 */
	public void deleteComment(Repository repo, User user, String commentId) throws IOException {
		IssueService issueService = new IssueService(githubAuthService.createClient(user));
		issueService.deleteComment(repo, commentId);
	}

	/*
	 * add Comment
	 * @param repo
	 * @param user
	 * @param issueId
	 * @param comment
	 * @return
	 * @throws IOException
	 */
	public Comment addComment(Repository repo, User user, String issueId,String comment) throws IOException {
		IssueService issueService = new IssueService(githubAuthService.createClient(user));
		return issueService.createComment(repo, issueId, comment);
	}

	/**
	 * @param repo repo
	 * @param user user
	 * @param issueId issueId
	 * @return
	 * @throws IOException
     */
	public PageIterator<Map<String,Object>> pageIssueEventsInMap(Repository repo, User user, String issueId) throws IOException {
		GithubIssueService issueService = new GithubIssueService(githubAuthService.createClient(user));
		return issueService.pageIssueEventsInMap(repo.getOwner().getLogin(), repo.getName(), Integer.valueOf(issueId));
	}

	/**
	 * get the issues count
	 * @param repo the repository
	 * @param user the user
	 * @param state open or closed
	 * @return the matched issues count
	 * @throws IOException
     */
	public int countIssues(Repository repo, User user, String state) throws IOException {
		GitHubClient client = githubAuthService.createClient(user);
		GitHubRequest request = new GitHubRequest();
		StringBuilder uriBuilder = new StringBuilder("/search/issues?per_page=1&q=repo:");
		uriBuilder.append(repo.getOwner().getLogin()).append("/").append(repo.getName());
		if(state != null){
			if("open".equalsIgnoreCase(state)){
				uriBuilder.append("+state:open");
			}else{
				uriBuilder.append("+state:closed");
			}
		}
		request.setUri(uriBuilder.toString());
		request.setType(HashMap.class);
		HashMap map = (HashMap)client.get(request).getBody();
		return (int)Double.parseDouble(map.get("total_count").toString());
	}

	/**
	 * search the content by keywords
	 * @param user
	 * @param searchContent
	 * @return
	 * @throws IOException
	 */
	public Pair<List<Issue>,Integer> searchContentByKeyWord(Repository repo, User user, String searchContent,int pageIndex,int pageSize) throws IOException{
		IssueService issueService = new IssueService(githubAuthService.createClient(user));
		GitHubClient client = githubAuthService.createClient(user);
		GithubIssueService githubIssueService = new GithubIssueService(client);
		return githubIssueService.searchContentByKeyWord(repo.generateId(), searchContent,pageIndex,pageSize);
	}



}
