package com.britesnow.samplesocial.service;

import static org.scribe.model.OAuthConstants.EMPTY_TOKEN;

import java.io.IOException;

import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.UserService;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import com.britesnow.samplesocial.dao.SocialIdEntityDao;
import com.britesnow.samplesocial.entity.SocialIdEntity;
import com.britesnow.samplesocial.oauth.OAuthServiceHelper;
import com.britesnow.samplesocial.oauth.OauthException;
import com.britesnow.samplesocial.oauth.ServiceType;
import com.google.inject.Inject;
import com.google.inject.Singleton;


@Singleton
public class GithubAuthService implements AuthService {

    @Inject
    private SocialIdEntityDao socialIdEntityDao;
    private OAuthService oAuthService;

    @Inject
    public GithubAuthService(OAuthServiceHelper oauthServiceHelper) {
        oAuthService = oauthServiceHelper.getOauthService(ServiceType.Github);
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
        Verifier verifier = new Verifier(verifierCode);
        Token accessToken = oAuthService.getAccessToken(EMPTY_TOKEN, verifier);
        if (accessToken.getToken() != null) {
            //get userinfo
            GitHubClient client = new GitHubClient();
            client.setOAuth2Token(accessToken.getToken());
            UserService userService = new UserService(client);
            User ghUser = userService.getUser();
            SocialIdEntity social = socialIdEntityDao.getSocialdentity(userId, ServiceType.Google);
            boolean newSocial = false;
            if (social == null) {
                social = new SocialIdEntity();
                newSocial = true;
            }
            social.setEmail(ghUser.getEmail());
            social.setUser_id(userId);
            social.setToken(accessToken.getToken());
            social.setService(ServiceType.Github);
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
