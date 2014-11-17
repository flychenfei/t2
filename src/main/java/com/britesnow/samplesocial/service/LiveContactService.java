package com.britesnow.samplesocial.service;

import java.util.ArrayList;
import java.util.List;
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

    public Map listContact(Boolean isFriend) {
        OAuthRequest request = oAuthService.createRequest(Verb.GET, CONTACT_ENDPOINT + "/me/contacts");
        Response response = request.send();
        Map profile = JsonUtil.toMapAndList(response.getBody());

        if(isFriend != null){
            List<Map> contactList = (List) profile.get("data");
            List contacts = new ArrayList();
            for(Map contact: contactList){
                Boolean isFriendProperty = (Boolean) contact.get("is_friend");
                if((isFriendProperty && isFriend) || (!isFriendProperty && !isFriend)){
                    contacts.add(contact);
                }
            }
            profile.put("data", contacts);
        }

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
        request.addBodyParameter("birth_day", (String) contact.get("birth_day"));
        request.addBodyParameter("birth_month", (String) contact.get("birth_month"));
        
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
