package com.britesnow.samplesocial.web;

import java.util.Map;

import net.sf.json.JSONObject;

import com.britesnow.samplesocial.dao.SocialIdEntityDao;
import com.britesnow.samplesocial.entity.User;
import com.britesnow.samplesocial.oauth.ServiceType;
import com.britesnow.samplesocial.service.SalesForceService;
import com.britesnow.snow.web.RequestContext;
import com.britesnow.snow.web.param.annotation.WebModel;
import com.britesnow.snow.web.param.annotation.WebParam;
import com.britesnow.snow.web.rest.annotation.WebGet;
import com.britesnow.snow.web.rest.annotation.WebPost;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class SFContactWebHandlers {
    
    @Inject
    private SocialIdEntityDao socialIdEntityDao;
    @Inject
    private SalesForceService salesforceService;
    
    @WebGet("/salesforce/listContacts")
    public Map listSFContacts(@WebModel Map m,
                               @WebParam("pageSize") Integer pageSize, @WebParam("pageIndex") Integer pageIndex,RequestContext rc) {
        User user = rc.getUser(User.class);
        String token = socialIdEntityDao.getSocialdentity(user.getId(), ServiceType.SalesForce).getToken();
        if(pageIndex == null){
            pageIndex = 0;
        }
        if(pageSize == null){
            pageSize = 10;
        }
        
        Map result = salesforceService.listContacts(token, pageIndex, pageSize);
        m.putAll(result);
        return m ;
    }
	
	@WebGet("/salesforce/getContact")
	public Map getContact(@WebModel Map m,@WebParam("id") String id,RequestContext rc) {
	    User user = rc.getUser(User.class);
	    String token = socialIdEntityDao.getSocialdentity(user.getId(), ServiceType.SalesForce).getToken();
	    JSONObject obj = salesforceService.getContact(token, id);
	    m.put("result", obj);
	    return m ;
	}
	
	@WebPost("/salesforce/saveContact")
	public Object saveSFContact(@WebModel Map m,@WebParam("id") String id,@WebParam("name") String name,RequestContext rc) {
	    User user = rc.getUser(User.class);
	    String token = socialIdEntityDao.getSocialdentity(user.getId(), ServiceType.SalesForce).getToken();
	    salesforceService.saveContact(token, id, name);
        return null;
	}
	
	@WebPost("/salesforce/deleteContact")
	public Object deleteSFContact(@WebModel Map m,@WebParam("id") String id,RequestContext rc) {
	    User user = rc.getUser(User.class);
        String token = socialIdEntityDao.getSocialdentity(user.getId(), ServiceType.SalesForce).getToken();
        salesforceService.deleteContact(token, id);
        return null;
	}
}
