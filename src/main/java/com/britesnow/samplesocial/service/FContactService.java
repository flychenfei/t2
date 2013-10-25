package com.britesnow.samplesocial.service;

import java.util.HashMap;
import java.util.List;

import com.britesnow.samplesocial.dao.ContactDao;
import com.britesnow.samplesocial.entity.Contact;
import com.britesnow.samplesocial.entity.User;
import com.britesnow.samplesocial.manager.OAuthManager;
import com.britesnow.samplesocial.oauth.ServiceType;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class FContactService {
    @Inject
    private ContactDao      contactDao;
    @Inject
    private FacebookService facebookService;
    @Inject
    private OAuthManager oAuthManager;

    public List getContactsByPage(User user, String query) {
        return contactDao.getContactsList(user,query);
    }

    public Contact addContact(String token, Long groupId, String fbid) {
        Contact c = contactDao.getContactByFbid(fbid);
        if (c == null) {
            c = new Contact();
        }
        c.setFbid(fbid);
        com.restfb.types.User user = facebookService.getUserInformation(token, fbid);
        c.setName(user.getName());
        c.setEmail(user.getEmail());
        c.setHometownname(user.getHometownName());
        
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("userId", user.getId()+"");
        map.put("access_token", token);
        map.put("email", user.getName());
        oAuthManager.setInfo(ServiceType.FaceBook, map);
        
        if (c.getId() == null) {
            contactDao.save(c);
        } else {
            contactDao.update(c);
        }
        return c;
    }

    public void deleteContact(String id) {
        contactDao.deleteContact(id);
    }
}
