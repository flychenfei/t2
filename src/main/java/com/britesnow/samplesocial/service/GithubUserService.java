package com.britesnow.samplesocial.service;

import java.io.IOException;

import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.UserService;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Verb;

import com.britesnow.samplesocial.entity.User;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class GithubUserService {

	private static String USER_INFO = "https://api.github.com/user";
	
	private static String PREFIX = "https://api.github.com";
	
	private static String EMAILS = "/user/emails";
	
	@Inject
	private YaoGithubAuthService githubAuthService;

	public String showUserInfoUrl(User user){
		return USER_INFO+"?access_token="+githubAuthService.getToken(user).getToken();
	}
	
	public String getUserInfo(User user) {
		OAuthRequest request = githubAuthService.createRequest(Verb.GET, showUserInfoUrl(user));
		Response response = request.send();
	    return response.getBody();
	}
	
	public org.eclipse.egit.github.core.User getGithubUser(User user) throws IOException{
		UserService service = new UserService(githubAuthService.createClient(user));
		return service.getUser();
	}
	
	public String  getEmails(User user){
		OAuthRequest request = githubAuthService.createRequest(Verb.GET, PREFIX+EMAILS);
		request.addHeader("Accept", "application/vnd.github.v3");
		addToken(request,user);
		Response response = request.send();
	    return response.getBody();
	}
	
	public String addEmail(String email,User user){
		UserService service = new UserService(githubAuthService.createClient(user));
		try {
			service.addEmail(email);
		} catch (IOException e) {
			if(e.getMessage().startsWith("Validation Failed"))
			return "Error adding '"+email+"' email is already in use";
			return "Error adding '"+email+"'Unknown error.";
		}
		return "adding '"+email+"' successfully";
	}
	
	public String deleteEmail(String email,User user){
		GitHubClient client = new GitHubClient();
		client.setOAuth2Token(githubAuthService.getToken(user).getToken());
		client.setUserAgent("GitHubJava/2.1.0");
		UserService service = new UserService(client);
		try {
			service.removeEmail(email);
		} catch (IOException e) {
			return "Error deleting '"+email+"',There at least on Email required.";
		}
		return "deleting '"+email+"' successfully";
	}
	
	public void addToken(OAuthRequest request,User user){
		if(request.getVerb()==Verb.GET)
			request.addQuerystringParameter("access_token", githubAuthService.getToken(user).getToken());
		else{
			request.addBodyParameter("access_token", githubAuthService.getToken(user).getToken());
		}
	}
}
