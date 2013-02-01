package com.britesnow.samplesocial.service;

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
		System.out.println(response.getBody());
	    return response.getBody();
	}
}
