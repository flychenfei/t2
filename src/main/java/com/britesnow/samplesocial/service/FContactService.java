package com.britesnow.samplesocial.service;

import java.util.List;

import com.britesnow.samplesocial.dao.ContactDao;
import com.britesnow.samplesocial.entity.Contact;
import com.britesnow.samplesocial.entity.User;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class FContactService {
    @Inject
    private ContactDao      contactDao;
    @Inject
    private FacebookService facebookService;

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
