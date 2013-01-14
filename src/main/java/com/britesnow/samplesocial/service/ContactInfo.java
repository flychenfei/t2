package com.britesnow.samplesocial.service;


import com.google.gdata.data.Content;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.contacts.Birthday;
import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.data.contacts.GroupMembershipInfo;
import com.google.gdata.data.extensions.*;

import java.util.List;

public class ContactInfo {
    private String fullName;
    private String givenName;
    private String familyName;
    private String phone;
    private String bir;
    private String groupId;
    private String email;
    private String notes;
    private String id;
    private String etag;

    public ContactInfo() {
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBir() {
        return bir;
    }

    public void setBir(String bir) {
        this.bir = bir;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public static ContactInfo from(ContactEntry entry) {
        ContactInfo info = new ContactInfo();
        Name name = entry.getName();
        if (name != null) {
            if (name.getFamilyName() != null)
                info.setFamilyName(name.getFamilyName().getValue());
            if (name.getFullName() != null)
                info.setFullName(name.getFullName().getValue());
            if (name.getGivenName() != null)
                info.setGivenName(name.getGivenName().getValue());
        }
        List<PhoneNumber> phoneNumbers = entry.getPhoneNumbers();
        if (phoneNumbers.size() > 0) {
            info.setPhone(phoneNumbers.get(0).getPhoneNumber());
        }
        if (entry.getBirthday() != null)
            info.setBir(entry.getBirthday().getValue());
        List<GroupMembershipInfo> groups = entry.getGroupMembershipInfos();
        if (groups.size() > 0) {
            info.setGroupId(groups.get(0).getHref());
        }
        List<Email> emails = entry.getEmailAddresses();
        if (emails.size() > 0) {
            info.setEmail(emails.get(0).getAddress());
        }
        Content content = entry.getContent();
        if (content != null && content.getType() <= 5) {
            info.setNotes(entry.getTextContent().getContent().getPlainText());
        }
        if (entry.getId() != null) {
            info.setId(entry.getId());
        }
        if (entry.getEtag() != null) {
            info.setEtag(entry.getEtag());
        }

        return info;
    }

    public ContactEntry to() {
        ContactEntry contactEntry = new ContactEntry();

        Name name = new Name();
        if (this.getFullName() != null) {
            name.setFullName(new FullName(this.getFullName(), null));
        }
        if (this.getGivenName() != null) {
            name.setGivenName(new GivenName(this.getGivenName(), null));
        }
        if (this.getFamilyName() != null) {
            name.setFamilyName(new FamilyName(this.getFamilyName(), ""));
        }
        contactEntry.setName(name);
        if (this.getNotes() != null) {
            contactEntry.setContent(new PlainTextConstruct(this.getNotes()));
        }
        //set email
        if (this.getEmail() != null) {
            Email primaryMail = new Email();
            primaryMail.setAddress(this.getEmail());
            primaryMail.setRel("http://schemas.google.com/g/2005#home");
            primaryMail.setPrimary(true);
            contactEntry.addEmailAddress(primaryMail);
        }

        if (this.getPhone() != null) {
            PhoneNumber pn = new PhoneNumber();
            pn.setPhoneNumber(this.getPhone());
            pn.setPrimary(true);
            pn.setRel("http://schemas.google.com/g/2005#work");
            contactEntry.addPhoneNumber(pn);
        }
        //Add to a Group
        if (this.getGroupId() != null) {
            GroupMembershipInfo gm = new GroupMembershipInfo();
            gm.setHref(this.getGroupId());
            contactEntry.addGroupMembershipInfo(gm);
        }
        if (this.getBir() != null) {
            Birthday b = new Birthday(this.getBir());
            contactEntry.setBirthday(b);
        }
        if (this.getId() != null) {
            contactEntry.setId(this.getId());
        }
        if (this.getEtag() != null) {
            contactEntry.setEtag(getEtag());
        }
        return contactEntry;
    }
}
