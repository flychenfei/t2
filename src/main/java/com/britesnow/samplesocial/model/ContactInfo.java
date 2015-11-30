package com.britesnow.samplesocial.model;


import com.google.gdata.data.Content;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.contacts.Birthday;
import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.data.contacts.GroupMembershipInfo;
import com.google.gdata.data.extensions.*;
import org.apache.commons.lang.StringUtils;

import java.util.List;

public class ContactInfo {
    private String givenName;
    private String familyName;
    private String phone;
    private String postalAddress;
    private String imAddress;
    private String bir;
    private String groupId;
    private String email;
    private String notes;
    private String id;
    private String etag;

    private List<String> groups = null;
    private String groupstr = null;

    public ContactInfo() {
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

    public String getPostalAddress() { return postalAddress; }

    public void setPostalAddress(String postalAddress) { this.postalAddress = postalAddress;}

    public String getImAddress() { return imAddress; }

    public void setImAddress(String imAddress) { this.imAddress = imAddress; }

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

    public List<String> getGroups() {
        return groups;
    }



    public void setGroups(List<String> groups) {
        this.groups = groups;
    }

    public static ContactInfo from(ContactEntry entry) {
        ContactInfo info = new ContactInfo();
        //set name
        Name name = entry.getName();
        if (name != null) {
            if (name.getFamilyName() != null)
                info.setFamilyName(name.getFamilyName().getValue());
            if (name.getGivenName() != null)
                info.setGivenName(name.getGivenName().getValue());
        }
        //set phone number
        List<PhoneNumber> phoneNumbers = entry.getPhoneNumbers();
        if (phoneNumbers.size() > 0) {
            info.setPhone(phoneNumbers.get(0).getPhoneNumber());
        }
        //set birthday
        if (entry.getBirthday() != null)
            info.setBir(entry.getBirthday().getValue());
        //set imAddress
        List<Im> ims = entry.getImAddresses();
        if(ims.size() > 0){
            info.setImAddress(ims.get(0).getAddress());
        }

        List<StructuredPostalAddress> pa = entry.getStructuredPostalAddresses();
        if(pa.size() > 0){
            info.setPostalAddress(pa.get(0).getFormattedAddress().getValue());
        }

        //set groups
        List<GroupMembershipInfo> groups = entry.getGroupMembershipInfos();
        if (groups.size() > 0) {
            info.setGroupId(groups.get(0).getHref());
        }
        //set emails
        List<Email> emails = entry.getEmailAddresses();
        if (emails.size() > 0) {
            info.setEmail(emails.get(0).getAddress());
        }
        //set content
        Content content = entry.getContent();
        if (content != null && content.getType() <= 5) {
            info.setNotes(entry.getTextContent().getContent().getPlainText());
        }
        //set id
        if (entry.getId() != null) {
            info.setId(entry.getId());
        }
        //set etag
        if (entry.getEtag() != null) {
            info.setEtag(entry.getEtag());
        }

        return info;
    }

    public ContactEntry to() {
        ContactEntry contactEntry = new ContactEntry();
        //set name
        Name name = new Name();

        if (StringUtils.isNotEmpty(this.getGivenName())) {
            name.setGivenName(new GivenName(this.getGivenName(), null));
        }
        if (StringUtils.isNotEmpty(this.getFamilyName())) {
            name.setFamilyName(new FamilyName(this.getFamilyName(), ""));
        }
        contactEntry.setName(name);
        if (this.getNotes() != null) {
            contactEntry.setContent(new PlainTextConstruct(this.getNotes()));
        }

        //set email
        if (StringUtils.isNotEmpty(this.getEmail())) {
            Email primaryMail = new Email();
            primaryMail.setAddress(this.getEmail());
            primaryMail.setRel("http://schemas.google.com/g/2005#home");
            primaryMail.setPrimary(true);
            contactEntry.addEmailAddress(primaryMail);
        }

        //set phone
        if (StringUtils.isNotEmpty(this.getPhone())) {
            PhoneNumber pn = new PhoneNumber();
            pn.setPhoneNumber(this.getPhone());
            pn.setPrimary(true);
            pn.setRel("http://schemas.google.com/g/2005#work");
            contactEntry.addPhoneNumber(pn);
        }

        //set postal address
        if (StringUtils.isNotEmpty(this.getPostalAddress())) {
            StructuredPostalAddress postalAddress = new StructuredPostalAddress();
            postalAddress.setFormattedAddress(new FormattedAddress(this.getPostalAddress()));
            postalAddress.setPrimary(true);
            postalAddress.setRel("http://schemas.google.com/g/2005#work");
            contactEntry.addStructuredPostalAddress(postalAddress);
        }

        //set imAddress
        if (StringUtils.isNotEmpty(this.getImAddress())) {
            Im imAddress = new Im();
            imAddress.setAddress(this.getImAddress());
            imAddress.setRel("http://schemas.google.com/g/2005#home");
            contactEntry.addImAddress(imAddress);
        }

        //Add to a Group
        if (StringUtils.isNotEmpty(this.getGroupId())) {
            GroupMembershipInfo gm = new GroupMembershipInfo();
            gm.setHref(this.getGroupId());
            contactEntry.addGroupMembershipInfo(gm);
        }

        if (this.getGroups() != null) {
            for(int i = 0; i < this.getGroups().size(); i++){
                GroupMembershipInfo gm = new GroupMembershipInfo();
                gm.setHref(this.getGroups().get(i));
                contactEntry.addGroupMembershipInfo(gm);
            }
        }
        //set birthday
        if (StringUtils.isNotEmpty(this.getBir())) {
            Birthday b = new Birthday(this.getBir());
            contactEntry.setBirthday(b);
        }
        //set id
        if (StringUtils.isNotEmpty(this.getId())) {
            contactEntry.setId(this.getId());
        }
        //set etag
        if (StringUtils.isNotEmpty(this.getEtag())) {
            contactEntry.setEtag(getEtag());
        }
        return contactEntry;
    }
}
