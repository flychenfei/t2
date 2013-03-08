package com.britesnow.samplesocial.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;
import org.eclipse.egit.github.core.Download;
import org.eclipse.egit.github.core.DownloadResource;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.service.DownloadService;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.eclipse.egit.github.core.util.EncodingUtils;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Verb;

import com.britesnow.samplesocial.entity.User;
import com.britesnow.snow.util.JsonUtil;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class GithubRepositoriesService {
	
	private static String PREFIX = "https://api.github.com";
	
	@Inject
	private YaoGithubAuthService githubAuthService;
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
				PREFIX+"/repos/"+githubUserService.getGithubUser(user).getLogin()+"/"+repo+
				"/contents"+path+"?access_token="+githubAuthService.getToken(user).getToken());
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
		System.out.println(repositoryService.getForks(repo).size());
		return repositoryService.getForks(repo);
	}
}
