package com.britesnow.samplesocial.web;


import com.britesnow.samplesocial.entity.User;
import com.britesnow.samplesocial.service.ContactInfo;
import com.britesnow.samplesocial.service.GContactService;
import com.britesnow.samplesocial.web.annotation.WebObject;
import com.britesnow.snow.web.RequestContext;
import com.britesnow.snow.web.handler.annotation.WebActionHandler;
import com.britesnow.snow.web.handler.annotation.WebModelHandler;
import com.britesnow.snow.web.param.annotation.WebModel;
import com.britesnow.snow.web.param.annotation.WebParam;
import com.britesnow.snow.web.param.annotation.WebUser;
import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.data.contacts.ContactGroupEntry;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Singleton
public class GoogleContactHandlers {
    private static Logger log = LoggerFactory.getLogger(GoogleContactHandlers.class);
    @Inject
    private GContactService gContactService;

    @WebModelHandler(startsWith = "/googleContacts")
    public void getContacts(@WebUser User user, @WebModel Map m, @WebParam("groupId") String groupId,
                            @WebParam("pageSize") Integer pageSize, @WebParam("pageIndex") Integer pageIndex,
                            RequestContext rc) throws Exception {
        List<ContactEntry> list = gContactService.getContactResults(user, groupId, pageIndex * pageSize + 1, pageSize);

        List<ContactInfo> infos = new ArrayList<ContactInfo>();
        for (ContactEntry contact : list) {
            infos.add(ContactInfo.from(contact));
        }

        m.put("result", infos);
        if (infos.size() == pageSize) {
            m.put("hasNext", true);
        }

    }

    @WebModelHandler(startsWith = "/googleGroups")
    public void getGroups(@WebModel Map m, @WebUser User user, RequestContext rc) throws Exception {
        List<ContactGroupEntry> list;
        list = gContactService.getGroupResults(user);

        m.put("result", list);

    }


    @WebActionHandler(name = "createContact")
    public WebResponse createContact(@WebUser User user, @WebObject ContactInfo contact) {
        boolean result = true;

            try {
                if (contact.getId() == null) {
                    gContactService.createContact(user,contact);
                } else {
                    gContactService.updateContactEntry(user, contact);
                }

            } catch (Exception e) {
                log.warn("create contact fail", e);
                return WebResponse.fail(e);
            }


        return WebResponse.success(result);
    }

    @WebActionHandler(name = "createGroup")
    public WebResponse createGroup(@WebUser User user, @WebParam("groupId") String groupId,
                           @WebParam("groupName") String groupName, @WebParam("etag") String etag) {
        boolean result = true;
            try {
                if (groupId == null) {
                    //create group
                    gContactService.createContactGroupEntry(user, groupName);
                } else {
                    //update group
                    gContactService.updateContactGroupEntry(user, groupId, etag, groupName);
                }

            } catch (Exception e) {
                log.warn(String.format("create Group %s fail", groupName), e);
                result = false;
                return WebResponse.fail(e);
            }


        return WebResponse.success(result);
    }

    @WebActionHandler(name = "deleteGroup")
    public WebResponse deleteGroup(@WebUser User user, @WebParam("groupId") String groupId, @WebParam("etag") String etag) {
        boolean result = false;
        if (user != null) {
            try {
                gContactService.deleteGroup(user, groupId, etag);
                result = true;
            } catch (Exception e) {
                log.warn(String.format("delete group %s fail", groupId), e);
                return WebResponse.fail(e);
            }
        }
        return WebResponse.success(result);
    }

    @WebActionHandler(name = "deleteContact")
    public WebResponse deleteContact(@WebUser User user, @WebParam("contactId") String contactId, @WebParam("etag") String etag) {
        boolean result = false;
        if (user != null) {
            try {
                gContactService.deleteContact(user, contactId, etag);
                result = true;
            } catch (Exception e) {
                log.warn(String.format("delete contact %s fail", contactId), e);
                return WebResponse.fail(e);
            }
        }
        return WebResponse.success(result);
    }

    @WebModelHandler(startsWith = "/getContact")
    public void getContact(@WebParam("contactId") String contactId,
                           @WebParam("etag") String etag, @WebModel Map m, @WebUser User user) {
        if (user != null && contactId != null) {
            try {
                ContactEntry entry = gContactService.getContactEntry(user, contactId);
                m.put("result", ContactInfo.from(entry));
            } catch (Exception e) {
                log.warn(String.format("get contact %s fail", contactId), e);
                m.put("result", false);
            }
        }
    }
}
