package com.britesnow.samplesocial.web;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;

import com.britesnow.samplesocial.dao.SocialIdEntityDao;
import com.britesnow.samplesocial.entity.Service;
import com.britesnow.samplesocial.entity.User;
import com.britesnow.samplesocial.util.Client;
import com.britesnow.snow.util.JsonUtil;
import com.britesnow.snow.web.RequestContext;
import com.britesnow.snow.web.param.annotation.WebModel;
import com.britesnow.snow.web.param.annotation.WebParam;
import com.britesnow.snow.web.rest.annotation.WebGet;
import com.britesnow.snow.web.rest.annotation.WebPost;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class SFContactWebHandlers {
    
    private  static final String SF_URL = "https://ap1.salesforce.com/services/data/v26.0";
    
    @Inject
    private SocialIdEntityDao socialIdEntityDao;
    
	@WebGet("/salesforce/listContacts")
	public Map listSFContacts(@WebModel Map m,RequestContext rc) {
	    User user = rc.getUser(User.class);
	    String token = socialIdEntityDao.getSocialdentity(user.getId(), Service.SalesForce).getToken();
	    String url = SF_URL+"/query?";
	    String sql = "SELECT Id, Name FROM Contact LIMIT 1000";
        sql = Client.encode(sql);
	    String params = "q="+sql;
	    HttpMethod method = new GetMethod(url+params);
	    method.addRequestHeader("Authorization", "OAuth "+token);
	    method.addRequestHeader("X-PrettyPrint", "1");
	    String response = Client.send(method);
	    JSONObject opts = (JSONObject) JsonUtil.toMapAndList(response);
	    m.put("result", opts.get("records"));
	    return m ;
	}
	
	@WebGet("/salesforce/getContact")
	public Map getContact(@WebModel Map m,@WebParam("id") String id,RequestContext rc) {
	    User user = rc.getUser(User.class);
	    String token = socialIdEntityDao.getSocialdentity(user.getId(), Service.SalesForce).getToken();
	    String url = SF_URL+"/query?";
	    String sql = "SELECT Id, Name FROM Contact WHERE Id='"+id+"'";
	    sql = Client.encode(sql);
	    String params = "q="+sql;
	    HttpMethod method = new GetMethod(url+params);
	    method.addRequestHeader("Authorization", "OAuth "+token);
	    method.addRequestHeader("X-PrettyPrint", "1");
	    String response = Client.send(method);
	    JSONObject opts = (JSONObject) JsonUtil.toMapAndList(response);
	    JSONArray array = (JSONArray) opts.get("records");
	    m.put("result", array.get(0));
	    return m ;
	}
	
	@WebPost("/salesforce/saveContact")
	public Object saveSFContact(@WebModel Map m,@WebParam("id") String id,@WebParam("name") String name,RequestContext rc) {
	    User user = rc.getUser(User.class);
	    String token = socialIdEntityDao.getSocialdentity(user.getId(), Service.SalesForce).getToken();
	    Map map = new HashMap();
	    map.put("lastName", name);
	    String url = null;
	    PostMethod method = null;
	    if(id != null && id.length() > 0){
	        url = SF_URL+"/sobjects/Contact/"+id;
            method = new PostMethod(url+"?_HttpMethod=PATCH");
	    }else{
	        url = SF_URL+"/sobjects/Contact/";
	        method = new PostMethod(url);
	    }
	    method.addRequestHeader("Authorization", "OAuth "+token);
        method.addRequestHeader("X-PrettyPrint", "1");
        method.addRequestHeader("Content-Type","application/json");
        try {
            method.setRequestEntity(new StringRequestEntity(JSONObject.fromObject(map).toString(),
                "application/json", "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try{
            Client.send(method);
        }catch(Exception e){
            
        }
        return null;
	}
	
	@WebPost("/salesforce/deleteContact")
	public Object deleteSFContact(@WebModel Map m,@WebParam("id") String id,RequestContext rc) {
	    User user = rc.getUser(User.class);
        String token = socialIdEntityDao.getSocialdentity(user.getId(), Service.SalesForce).getToken();
        String url = null;
        PostMethod method = null;
        if(id != null && id.length() > 0){
            url = SF_URL+"/sobjects/Contact/"+id;
            method = new PostMethod(url+"?_HttpMethod=DELETE");
            method.addRequestHeader("Authorization", "OAuth "+token);
            method.addRequestHeader("X-PrettyPrint", "1");
            try{
                Client.send(method);
            }catch(Exception e){
                
            }
        }
        return null;
	}
}
