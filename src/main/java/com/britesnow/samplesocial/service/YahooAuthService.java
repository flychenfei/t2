package com.britesnow.samplesocial.service;

import com.britesnow.samplesocial.dao.SocialIdEntityDao;
import com.britesnow.samplesocial.entity.SocialIdEntity;
import com.britesnow.samplesocial.oauth.OAuthServiceHelper;
import com.britesnow.samplesocial.oauth.OauthException;
import com.britesnow.samplesocial.oauth.ServiceType;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.scribe.model.*;
import org.scribe.oauth.OAuthService;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Singleton
public class YahooAuthService implements AuthService {

    @Inject
    private SocialIdEntityDao socialIdEntityDao;
    private OAuthService oAuthService;

    public static final String PROFILE_URL = "http://api.linkedin.com/v1/people/~:(email-address)";

    private final LoadingCache<String, Token> tokenCache;

    @Inject
    public YahooAuthService(OAuthServiceHelper oauthServiceHelper) {
        oAuthService = oauthServiceHelper.getOauthService(ServiceType.Yahoo);
        tokenCache = CacheBuilder.newBuilder().maximumSize(10000).expireAfterAccess(3, TimeUnit.MINUTES)
                .build(new CacheLoader<String, Token>() {
                    @Override
                    public Token load(String oauthCode) throws Exception {
                        return OAuthConstants.EMPTY_TOKEN;
                    }
                });
    }

    @Override
    public SocialIdEntity getSocialIdEntity(Long userId) {
        SocialIdEntity socialId = socialIdEntityDao.getSocialdentity(userId, ServiceType.Yahoo);
        if (socialId != null && socialId.getTokenDate().getTime() >= System.currentTimeMillis()) {
            return socialId;
        }
        //if result is null, need redo auth
        throw new OauthException(getAuthorizationUrl());
    }

    public String getAuthorizationUrl() {
        Token reqToken = oAuthService.getRequestToken();
        tokenCache.put(reqToken.getToken(), reqToken);
        return oAuthService.getAuthorizationUrl(reqToken);
    }

    public boolean updateAccessToken(String requestToken, String verifierCode, long userId)  {
        try {
            Verifier verifier = new Verifier(verifierCode);
            Token reqToken = tokenCache.get(requestToken);
            Token accessToken = oAuthService.getAccessToken(reqToken, verifier);
            if (accessToken.getToken() != null) {
                //get expire date
                String rawResponse = accessToken.getRawResponse();
                Pattern expire = Pattern.compile("oauth_authorization_expires_in=\\s*(\\d+)");
                Matcher matcher = expire.matcher(rawResponse);
                long expireDate = -1;
                if (matcher.find()) {
                    expireDate = System.currentTimeMillis() + (Integer.valueOf(matcher.group(1)) - 100) * 1000;
                }

/*                OAuthRequest request = new OAuthRequest(Verb.GET, PROFILE_URL);
                request.addHeader("x-li-format","json");
                oAuthService.signRequest(accessToken, request);
                Response response = request.send();
                Map map = JsonUtil.toMapAndList(response.getBody());*/

                SocialIdEntity social = socialIdEntityDao.getSocialdentity(userId, ServiceType.Yahoo);
                boolean newSocial = false;
                if (social == null) {
                    social = new SocialIdEntity();
                    newSocial = true;
                }
                social.setUser_id(userId);
                //social.setEmail((String) map.get("emailAddress"));
                social.setToken(accessToken.getToken());
                social.setSecret(accessToken.getSecret());
                social.setService(ServiceType.Yahoo);
                social.setTokenDate(new Date(expireDate));
                if (newSocial) {
                    socialIdEntityDao.save(social);
                } else {
                    socialIdEntityDao.update(social);
                }

                return true;
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }
}
