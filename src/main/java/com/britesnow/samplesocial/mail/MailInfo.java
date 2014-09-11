package com.britesnow.samplesocial.mail;


import java.util.ArrayList;
import java.util.List;

import javax.mail.Address;
import javax.mail.Message;

public class MailInfo {
    private Object id;
    private Object date;
    private String from;
    private String subject;
    private String content;
    private List<String> cc = null;
    private List attachments = null;
    private String threadId;
    private List<String> folderIds = null;
    private List<String> folderNames = null;

    public MailInfo(Object id, Object date, String from, String subject, Address[] ccAddress) {
        this.id = id;
        this.date = date;
        this.from = from;
        this.subject = subject;
        if(ccAddress != null && ccAddress.length >0){
            ArrayList cc = new ArrayList<String>();
            for(Address address : ccAddress){
                cc.add(address.toString());
            }
            this.cc = cc;
        }
    }


    public MailInfo(Message msg) {

    }

    public MailInfo() {
    }


    public Object getDate() {
        return date;
    }

    public void setDate(Object date) {
        this.date = date;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }


    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }


    public List getAttachments() {
        return attachments;
    }

    public void setAttachments(List attachments) {
        this.attachments = attachments;
    }

    public String getThreadId() {
        return threadId;
    }


    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    public List<String> getFolderIds() {
        return folderIds;
    }


    public void setFolderIds(List<String> folderIds) {
        this.folderIds = folderIds;
    }


    public List<String> getFolderNames() {
        return folderNames;
    }


    public void setFolderNames(List<String> folderNames) {
        this.folderNames = folderNames;
    }


    public List<String> getCc() {
        return cc;
    }


    public void setCc(List<String> cc) {
        this.cc = cc;
    }

    
}
