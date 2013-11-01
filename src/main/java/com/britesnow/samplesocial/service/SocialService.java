package com.britesnow.samplesocial.service;

import java.util.Map;


import com.britesnow.samplesocial.manager.OAuthManager;
import com.britesnow.samplesocial.model.SocialIdEntity;
import com.britesnow.samplesocial.oauth.ServiceType;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class SocialService {
	
	@Inject
    private OAuthManager oAuthManager;
    
    
    public SocialIdEntity getSocialIdEntityfromSession(ServiceType s){
    	SocialIdEntity socialId = new SocialIdEntity();
		Map<String, String> social = oAuthManager.getInfo(s);
		if(social == null){
		  return null;
		}
		socialId.setUser_id(Long.parseLong(social.get("userId").toString()));
        socialId.setEmail((String)social.get("email"));
        socialId.setToken((String)social.get("access_token"));
        socialId.setSecret((String)social.get("secret"));
        socialId.setService(s);
    	
    	return socialId;
    }

}
