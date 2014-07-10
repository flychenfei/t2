package com.britesnow.samplesocial.mail;


import java.util.List;

import javax.mail.Message;

public class MailInfo {
    private Object id;
    private Object date;
    private String from;
    private String subject;
    private String content;
    private List attachments = null;

    public MailInfo(Object id, Object date, String from, String subject) {
        this.id = id;
        this.date = date;
        this.from = from;
        this.subject = subject;
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

}
