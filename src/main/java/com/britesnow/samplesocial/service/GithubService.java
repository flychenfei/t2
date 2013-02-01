package com.britesnow.samplesocial.service;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Map;

import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;

import com.britesnow.samplesocial.entity.SocialIdEntity;
import com.britesnow.samplesocial.entity.User;
import com.britesnow.snow.util.JsonUtil;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class GithubService {

	private static String USER_INFO = "https://api.github.com/user";
	
	private static String PREFIX = "https://api.github.com";
	
	private static String EMAILS = "/user/emails";
	@Inject
	private YaoGithubAuthService yaoGithubAuthService;

	public Token getToken(User user) {
		SocialIdEntity soId = yaoGithubAuthService.getSocialIdEntity(user.getId());
		return new Token(soId.getToken(), soId.getSecret());
	}

	private OAuthRequest createRequest(Verb verb, String url) {
		OAuthRequest request = new OAuthRequest(verb, url);
		 request.addHeader("x-li-format","json");
		return request;
	}

	public String showUserInfoUrl(User user){
		return USER_INFO+"?access_token="+getToken(user).getToken();
	}
	
	public String getUserInfo(User user) {
		OAuthRequest request = createRequest(Verb.GET, showUserInfoUrl(user));
		Response response = request.send();
	    return response.getBody();
	}
	
	public String getRepositories(User user) {
		Map userInfo = JsonUtil.toMapAndList(getUserInfo(user));
		OAuthRequest request = createRequest(Verb.GET, userInfo.get("repos_url").toString());
		Response response = request.send();
	    return response.getBody();
	}
	
	public Map getAuthorizations(User user){
		OAuthRequest request = createRequest(Verb.GET, "https://api.github.com/authorizations/");
		Response response = request.send();
	    return JsonUtil.toMapAndList(response.getBody());
	}
	
	public String  getEmails(User user){
		OAuthRequest request = createRequest(Verb.GET, PREFIX+EMAILS);
		request.addHeader("Accept", "application/vnd.github.v3");
		addToken(request,user);
		Response response = request.send();
	    return response.getBody();
	}
	
	public String addEmail(String email,User user) throws IOException{
		OAuthRequest request = createRequest(Verb.POST, PREFIX+EMAILS);
		request.addHeader("Accept", "application/vnd.github.v3");
		request.addQuerystringParameter("access_token", getToken(user).getToken());
		Response response = request.send();
		System.out.println(response.getBody());
	    return response.getBody();
		/*URL url = new URL( PREFIX+EMAILS+"?access_token="+getToken(user));
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestProperty("Accept", "application/vnd.github.v3");
		con.setRequestProperty("User-Agent", "GitHubJava/2.1.0");
		con.setRequestMethod("POST");
		String[] mails = {"swbyzx@126.com"};
		sendParams(con, mails);
		System.out.println(con.getResponseMessage());
		return email;*/
	}
	
	public void addToken(OAuthRequest request,User user){
		if(request.getVerb()==Verb.GET)
			request.addQuerystringParameter("access_token", getToken(user).getToken());
		else{
			request.addBodyParameter("access_token", getToken(user).getToken());
		}
	}
	
	protected void sendParams(HttpURLConnection request, Object params)
			throws IOException {
		request.setDoOutput(true);
		if (params != null) {
			request.setRequestProperty("Content-Type", "application/json"
					+ "; charset=UTF-8" );
			byte[] data = JsonUtil.toJson(params).getBytes("UTF-8");
			request.setFixedLengthStreamingMode(data.length);
			BufferedOutputStream output = new BufferedOutputStream(
					request.getOutputStream(), 8192);
			try {
				output.write(data);
				output.flush();
			} finally {
				try {
					output.close();
				} catch (IOException ignored) {
				}
			}
		} else {
			request.setFixedLengthStreamingMode(0);
			request.setRequestProperty("Content-Length", "0");
		}
	}
}
