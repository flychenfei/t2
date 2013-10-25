package com.britesnow.samplesocial.manager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpSession;

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
}
