package com.britesnow.samplesocial.service;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import com.britesnow.samplesocial.model.ContactInfo;
import com.britesnow.samplesocial.model.SocialIdEntity;
import com.britesnow.snow.util.Pair;
import com.google.gdata.client.contacts.ContactQuery;
import com.google.gdata.client.contacts.ContactsService;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.data.contacts.ContactFeed;
import com.google.gdata.data.contacts.ContactGroupEntry;
import com.google.gdata.data.contacts.ContactGroupFeed;
import com.google.gdata.util.ServiceException;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class GContactService {
    @Inject
    GoogleAuthService authService;

    private final String BASE_CONTACTS_URL = "https://www.google.com/m8/feeds/contacts/default/full";
    private final String BASE_GROUP_URL = "https://www.google.com/m8/feeds/groups/default/full";

    public ContactGroupEntry createContactGroupEntry(String name) throws Exception {
        ContactGroupEntry group = new ContactGroupEntry();
        group.setTitle(new PlainTextConstruct(name));
        URL postUrl = new URL(BASE_GROUP_URL);
        return getContactsService().insert(postUrl, group);
    }

    public ContactGroupEntry getContactGroupEntry(String groupId) throws IOException, ServiceException {
        URL url = new URL(BASE_GROUP_URL + "/" + groupId);
        return getContactsService().getEntry(url, ContactGroupEntry.class);
    }
    public ContactEntry getContactEntry(String contactId) throws IOException, ServiceException {
        URL url = new URL(BASE_CONTACTS_URL + "/" + contactId);
        return getContactsService().getEntry(url, ContactEntry.class);
    }


    public Pair<List<ContactEntry>,Integer> getContactResults(String groupId, int startIndex, int count) throws ServiceException, IOException {
        URL feedUrl = new URL(BASE_CONTACTS_URL);
        ContactQuery myQuery = new ContactQuery(feedUrl);
        if (groupId != null) {
            myQuery.setStringCustomParameter("group", groupId);
        }
        myQuery.setStartIndex(startIndex);
        myQuery.setMaxResults(count);
        ContactFeed resultFeed = getContactsService().query(myQuery, ContactFeed.class);
        int total = resultFeed.getTotalResults();
        return new Pair<List<ContactEntry>, Integer>(resultFeed.getEntries(), total);
    }
    public Pair<List<ContactEntry>,Integer> searchContactResults(String contactName, int startIndex, int count) throws ServiceException, IOException {
        URL feedUrl = new URL(BASE_CONTACTS_URL);
        ContactQuery myQuery = new ContactQuery(feedUrl);
        if (contactName != null) {
            myQuery.setFullTextQuery(contactName);
        }
        myQuery.setStartIndex(startIndex);
        myQuery.setMaxResults(count);
//       myQuery.setGroup(String.format(BASE_GROUP_URL + "/" + groupId).replace("full","base"));
//        myQuery.setGroup("https://www.google.com/m8/feeds/groups/woofgl%40gmail.com/base/6");
        ContactFeed resultFeed = getContactsService().query(myQuery, ContactFeed.class);
        int total = resultFeed.getTotalResults();
        return new Pair<List<ContactEntry>, Integer>(resultFeed.getEntries(), total);
    }


    public Pair<List<ContactGroupEntry>, Integer> getGroupResults() throws IOException, ServiceException {
        URL feedurUrl = new URL(BASE_GROUP_URL);
        ContactGroupFeed contactGroupFeed = getContactsService().getFeed(feedurUrl, ContactGroupFeed.class);
        int count = contactGroupFeed.getTotalResults();
        return new Pair<List<ContactGroupEntry>, Integer>(contactGroupFeed.getEntries(), count);
    }



    public ContactEntry createContact(ContactInfo contact)
            throws ServiceException, IOException {
        ContactEntry contactEntry = contact.to();
        //Add process
        URL postUrl = new URL(BASE_CONTACTS_URL);
        return getContactsService().insert(postUrl, contactEntry);

    }


    public void deleteGroup(String groupId, String etag) throws IOException, ServiceException {
        String url = String.format("%s/%s", BASE_GROUP_URL, groupId);
        getContactsService().delete(new URL(url), etag);
    }
    public void deleteContact(String contactId, String etag) throws IOException, ServiceException {
        String url = String.format("%s/%s", BASE_CONTACTS_URL, contactId);
        getContactsService().delete(new URL(url), etag);
    }

    public void updateContactGroupEntry(String groupId, String etag, String groupName) throws IOException, ServiceException {
        String url = String.format("%s/%s", BASE_GROUP_URL, groupId);
        ContactGroupEntry group = new ContactGroupEntry();
        group.setTitle(new PlainTextConstruct(groupName));
        getContactsService().update(new URL(url), group, etag);
    }

    public void updateContactEntry(ContactInfo contact) throws IOException, ServiceException {
        String url = String.format("%s/%s", BASE_CONTACTS_URL, contact.getId());
        getContactsService().update(new URL(url), contact.to());
    }

    private ContactsService getContactsService() {
        SocialIdEntity social = authService.getSocialIdEntity();
        //System.out.println("---------"+social.getToken());
        if (social != null) {
            ContactsService service = new ContactsService("Contacts Sample");
            service.setHeader("Authorization", "Bearer " + social.getToken());
            return service;
        }
        return null;
    }
}
