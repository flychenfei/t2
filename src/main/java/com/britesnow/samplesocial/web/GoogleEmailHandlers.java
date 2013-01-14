package com.britesnow.samplesocial.web;


import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeUtility;

import com.britesnow.samplesocial.entity.User;
import com.britesnow.samplesocial.mail.MailInfo;
import com.britesnow.samplesocial.service.GMailService;
import com.britesnow.snow.util.Pair;
import com.britesnow.snow.web.RequestContext;
import com.britesnow.snow.web.handler.annotation.WebActionHandler;
import com.britesnow.snow.web.handler.annotation.WebModelHandler;
import com.britesnow.snow.web.param.annotation.WebModel;
import com.britesnow.snow.web.param.annotation.WebParam;
import com.britesnow.snow.web.param.annotation.WebUser;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class GoogleEmailHandlers {
    @Inject
    GMailService gMailService;

    @WebModelHandler(startsWith = "/getFolders")
    public void listFolders(@WebUser User user, @WebModel Map m) throws Exception {
        Folder[] folders = gMailService.listFolders(user);
        List list = new ArrayList();
        for (Folder folder : folders) {
            Map map = new HashMap();
            map.put("name", folder.getName());
            map.put("fullName", folder.getFullName());
            list.add(map);
        }
        m.put("result", list);
    }

    @WebModelHandler(startsWith = "/getEmails")
    public void listEmails(@WebUser User user,
                           @WebModel Map m, @WebParam("folderName") String folderName,
                           @WebParam("pageSize") Integer pageSize, @WebParam("pageIndex") Integer pageIndex) throws Exception {
        Pair<Integer, Message[]> result = gMailService.listMails(user, "inbox", pageSize*pageIndex+1, pageSize);

        List<MailInfo> mailInfos = new ArrayList<MailInfo>();

        for (Message message : result.getSecond()) {
            MailInfo info = buildMailInfo(message);
            mailInfos.add(0, info);
        }
        m.put("result", mailInfos);
        m.put("result_count", result.getFirst());
    }

    @WebModelHandler(startsWith = "/getEmails")
    public void getEmail(@WebUser User user, @WebModel Map m, @WebParam("id") Integer id) throws Exception {
        Message message = gMailService.getEmail(user, id);
        MailInfo info = buildMailInfo(message);
        info.setContent(getContent(message));
        m.put("result", info);
    }

    private MailInfo buildMailInfo(Message message) throws MessagingException, UnsupportedEncodingException {
        return new MailInfo(message.getMessageNumber(), message.getSentDate().getTime(),
                decodeText(message.getFrom()[0].toString()), message.getSubject());
    }

    @WebActionHandler(name = "deleteEmail")
    public Object deleteEmail(@WebUser User user,
                              @WebParam("id") Integer id, RequestContext rc) throws Exception {
        gMailService.deleteEmail(user, id);
        Map map = new HashMap();
        map.put("result", true);
        return map;
    }


    private String decodeText(String text) throws UnsupportedEncodingException {
        if (text == null) {
            return null;
        }
        if (text.startsWith("=?GB") || text.startsWith("=?gb")) {
            text = MimeUtility.decodeText(text);
        } else {
            text = new String(text.getBytes("ISO8859_1"));
        }
        return text;

    }

    private String getContent(Message message) throws Exception {
        StringBuffer str = new StringBuffer();
        if (message.isMimeType("text/plain"))
            str.append(message.getContent().toString());
        if (message.isMimeType("multipart/alternative")) {
            Multipart part = (Multipart) message.getContent();
            str.append(part.getBodyPart(1).getContent().toString());
        }
        if (message.isMimeType("multipart/related")) {
            Multipart part = (Multipart) message.getContent();
            Multipart cpart = (Multipart) part.getBodyPart(0).getContent();
            str.append(cpart.getBodyPart(1).getContent().toString());
        }
        if (message.isMimeType("multipart/mixed")) {
            Multipart part = (Multipart) message.getContent();
            if (part.getBodyPart(0).isMimeType("text/plain")) {
                str.append(part.getBodyPart(0).getContent());
            }
            if (part.getBodyPart(0).isMimeType("multipart/alternative")) {
                Multipart cpart = (Multipart) part.getBodyPart(0).getContent();
                str.append(cpart.getBodyPart(1).getContent());
            }
        }
        return str.toString();
    }

    @WebActionHandler
    public Object sendMail(@WebUser User user,
                           @WebModel Map m, @WebParam("subject") String subject,
                           @WebParam("content") String content, @WebParam("to") String to, RequestContext rc) throws Exception {
        gMailService.sendMail(user, subject, content, to);
        return null;
    }

    @WebModelHandler(startsWith = "/searchEmails")
    public void search(@WebUser User user,
                       @WebModel Map m, @WebParam("subject") String subject, @WebParam("from") String from) throws Exception {

        Message[] msgs = gMailService.search(user, subject, from);
        List<MailInfo> infos = new ArrayList<MailInfo>();
        if (msgs.length > 0) {

            for (Message msg : msgs) {
                infos.add(buildMailInfo(msg));
            }
        }

        m.put("result", infos);
        m.put("success", true);
    }

}
