package com.britesnow.samplesocial.service;

import java.util.Map;

import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Verb;

import com.britesnow.snow.util.JsonUtil;
import com.google.gdata.util.common.base.StringUtil;
import com.google.inject.Inject;
import com.google.inject.Singleton;


@Singleton
public class LiveContactService {
    public static final String CONTACT_ENDPOINT = "https://apis.live.net/v5.0";
    
    @Inject
	private LiveAuthService oAuthService;

    public Map getUserInfo() {
        OAuthRequest request = oAuthService.createRequest(Verb.GET, LiveAuthService.PROFILE_ENDPOINT);
        Response response = request.send();
        Map profile = JsonUtil.toMapAndList(response.getBody());
        return profile;
    }

    public Map saveContact(Map contact, String contactId) {
        OAuthRequest request = null;
        if(StringUtil.isEmpty(contactId)){
            request = oAuthService.createRequest(Verb.POST, CONTACT_ENDPOINT + "/me/contacts");
        }else{
            request = oAuthService.createRequest(Verb.PUT, CONTACT_ENDPOINT + "/" + contactId);
        }
        request.addBodyParameter("first_name", (String) contact.get("first_name"));
        request.addBodyParameter("last_name", (String) contact.get("last_name"));
        Response response = request.send();
        Map contactInfo = JsonUtil.toMapAndList(response.getBody());
        return contactInfo;
    }


    public Map getContact(String contactId) {
        OAuthRequest request = oAuthService.createRequest(Verb.GET, CONTACT_ENDPOINT + "/" + contactId);
        Response response = request.send();
        Map contactInfo = JsonUtil.toMapAndList(response.getBody());
        return contactInfo;
    }




}
