package com.britesnow.samplesocial.dao;

import java.util.List;

import com.britesnow.samplesocial.entity.Contact;
import com.britesnow.samplesocial.entity.User;

public class ContactDao extends BaseHibernateDao<Contact> {
    public Contact getContactByFbid(String fbid) {
        String hql = "from Contact where fbid=?";
        List ls = search(hql, fbid);
        if (ls.size() > 0) {
            return (Contact) ls.get(0);
        }
        return null;
    }

    public List getContactsList(User user) {
        String hql = "from Contact where 1=1";
        List ls = search(hql);
        return ls;
    }

    public void deleteContact(String id) {
        Contact c = get(new Long(id));
        delete(c);
    }
}
