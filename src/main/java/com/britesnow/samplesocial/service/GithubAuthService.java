package com.britesnow.samplesocial.service;

import static org.scribe.model.OAuthConstants.EMPTY_TOKEN;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.UserService;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import com.britesnow.samplesocial.dao.SocialIdEntityDao;
import com.britesnow.samplesocial.entity.SocialIdEntity;
import com.britesnow.samplesocial.manager.OAuthManager;
import com.britesnow.samplesocial.oauth.OAuthServiceHelper;
import com.britesnow.samplesocial.oauth.OauthException;
import com.britesnow.samplesocial.oauth.ServiceType;
import com.britesnow.snow.web.binding.ApplicationProperties;
import com.google.inject.Inject;
import com.google.inject.Singleton;


@Singleton
public class GithubAuthService implements AuthService {

    @Inject
    private SocialIdEntityDao socialIdEntityDao;
    private OAuthService oAuthService;
    private Map configMap;
    @Inject
    private OAuthManager oAuthManager;
    
    @Inject
    public GithubAuthService(OAuthServiceHelper oauthServiceHelper, @ApplicationProperties Map configMap) {
        oAuthService = oauthServiceHelper.getOauthService(ServiceType.Github);
        this.configMap = configMap;
    }
    
    @Override
    public SocialIdEntity getSocialIdEntity(Long userId) {
        SocialIdEntity socialId = socialIdEntityDao.getSocialdentity(userId, ServiceType.Github);
        if (socialId != null) {
            return socialId;
        }
        //if result is null, need redo auth
        throw new OauthException(getAuthorizationUrl());
    }

    public String getAuthorizationUrl() {
        return oAuthService.getAuthorizationUrl(EMPTY_TOKEN);
    }

    public boolean updateAccessToken(String verifierCode, long userId) throws IOException {
    	OAuthRequest request = new OAuthRequest(Verb.POST, "https://github.com/login/oauth/access_token");
    	String prefix = "github";
    	request.addBodyParameter("code", verifierCode);
    	request.addBodyParameter("client_id", configMap.get(prefix+".client_id").toString());
    	request.addBodyParameter("client_secret",  configMap.get(prefix+".secret").toString());
    	String tokenString = request.send().getBody();
    	String token="";
    	String[] tokenArray = tokenString.split("&");
    	for(String s:tokenArray){
    		if(s.startsWith("access_token")){
    			token = s.substring(13);
    		}
    	}
    	Token accessToken  = new Token(token, configMap.get(prefix+".secret").toString());
    	OAuthRequest requestAccessToken = new OAuthRequest(Verb.GET,"https://api.github.com/user");
    	requestAccessToken.addQuerystringParameter("access_token", token);
    	//String accessTokenString = requestAccessToken.send().getBody();

    	//    	Verifier verifier = new Verifier(verifierCode);
        //        Token accessToken = oAuthService.getAccessToken(EMPTY_TOKEN, verifier);
    	
        if (accessToken.getToken() != null) {
            //get userinfo
            GitHubClient client = new GitHubClient();
            client.setOAuth2Token(accessToken.getToken());
            UserService userService = new UserService(client);
            User ghUser = userService.getUser();
            SocialIdEntity social = socialIdEntityDao.getSocialdentity(userId, ServiceType.Github);
            boolean newSocial = false;
            if (social == null) {
                social = new SocialIdEntity();
                newSocial = true;
            }
            social.setEmail(ghUser.getEmail());
            social.setUser_id(userId);
            social.setToken(accessToken.getToken());
            social.setService(ServiceType.Github);
            social.setSecret( configMap.get(prefix+".secret").toString());
            
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("userId", userId+"");
            map.put("access_token", accessToken.getToken());
            map.put("secret", configMap.get(prefix+".secret").toString());
            oAuthManager.setInfo(ServiceType.Github, map);
            
            if (newSocial) {
                socialIdEntityDao.save(social);
            } else {
                socialIdEntityDao.update(social);
            }
            return true;
        }
        throw new OauthException(getAuthorizationUrl());

    }
}
