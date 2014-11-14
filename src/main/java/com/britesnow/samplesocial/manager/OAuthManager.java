package com.britesnow.samplesocial.manager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.britesnow.samplesocial.entity.User;
import com.britesnow.samplesocial.model.SocialIdEntity;
import com.britesnow.samplesocial.oauth.ServiceType;
import com.britesnow.snow.web.CurrentRequestContextHolder;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class OAuthManager {
    @Inject
    private CurrentRequestContextHolder currentRequestContextHolder;
    
    private HttpSession getSession(){
    	return currentRequestContextHolder.getCurrentRequestContext().getReq().getSession();
    	
    }
	@SuppressWarnings("unchecked")
	public Map<String, String> getInfo(ServiceType serviceType){
    	HttpSession session = getSession();
    	return (Map)session.getAttribute(serviceType.toString());
    }
    public void setInfo(ServiceType serviceType,Map<String, String> map){
    	HttpSession session = getSession();
    	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    	String token_date = dateFormat.format(new Date());
    	map.put("token_date", token_date);
    	session.setAttribute(serviceType.toString(), map);
    }
    
    public User getUserInfo(String user){
    	HttpSession session = getSession();
    	return (User) session.getAttribute(user);
    }
    public void setUserInfo(String userkey,User user){
    	HttpSession session = getSession();
    	session.setAttribute(userkey, user);
    }
    
    public SocialIdEntity getSocialIdEntityfromSession(ServiceType s){
    	SocialIdEntity socialId = new SocialIdEntity();
		Map<String, String> social = getInfo(s);
		if(social == null){
		  return null;
		}
		//socialId.setUser_id(Long.parseLong(social.get("userId").toString()));
        socialId.setEmail((String)social.get("email"));
        socialId.setToken((String)social.get("access_token"));
        socialId.setSecret((String)social.get("secret"));
        socialId.setService(s);
    	
    	return socialId;
    }
}
