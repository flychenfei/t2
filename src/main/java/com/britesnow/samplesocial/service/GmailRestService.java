package com.britesnow.samplesocial.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.britesnow.samplesocial.mail.MailInfo;
import com.britesnow.snow.util.Pair;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePartHeader;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class GmailRestService {

    @Inject
    GoogleAuthService authService;
    
    private Gmail gmail = null;

    public Pair<String, List<MailInfo>> gmailSearch(String subject, String from, String to, 
    		String body, String sDate , String eDate, String srDate , String erDate,
    		String label, String hasAttachment , String attachmentName , String cc ,
    		String list, String hasCircle , String circle , String chatContent ,
    		String unread,String category , String deliveredTo , String rfc822msgid ,
    		Integer minSize, Integer maxSize, String start, int count) throws Exception  {
    	
        Gmail gmail = getGmailClient();
        if("0".equals(start)){
            start = "";
        }
        StringBuilder query = new StringBuilder();
    
        if (subject != null) {
            query.append("subject:");
            query.append(subject);
        }
        if (from != null) {
            query.append(" from:");
            query.append(from);
        }
        if (to != null) {
            query.append(" to:");
            query.append(to);
        }
        if (body != null) {
            query.append(" \"");
            query.append(body);
            query.append("\"");
        }
        if (sDate != null) {
            query.append(" after:");
            query.append(sDate);
        }
        if (eDate != null) {
            query.append(" before:");
            query.append(eDate);
        }
        if (srDate != null) {
            query.append(" after:");
            query.append(srDate);
        }
        if (erDate != null) {
            query.append(" before:");
            query.append(erDate);
        }
        if (label != null) {
            query.append(" label:");
        	query.append(label);
        }
        if (hasAttachment !=null) {
            query.append(" has:attachment");
        }
        if (attachmentName != null) {
            query.append(" filename:");
            query.append(attachmentName);
        }
        if (cc != null) {
            query.append(" cc:");
        	query.append(cc);
        }
        if (minSize != null) {
            query.append(" larger:");
            query.append(minSize.toString());
        }
        if (maxSize != null) {
            query.append(" smaller:");
            query.append(maxSize.toString());
        }
        if (list != null) {
            query.append(" list:");
            query.append(list);
        }
        if (hasCircle != null) {
            query.append(" has:circle");
        }
        if (circle != null) {
            query.append(" circle:");
            query.append(circle);
        }
        if (chatContent != null) {
            query.append(" is:chat ");
            query.append(chatContent);
        }
        if (unread != null) {
            query.append(" is:unread");
        }
        if (category != null) {
            query.append(" category:");
            query.append(category);
        }
        if (deliveredTo != null) {
            query.append(" deliveredTo:");
            query.append(deliveredTo);
        }
        if (rfc822msgid != null) {
            query.append(" rfc822msgid:");
            query.append(rfc822msgid);
        }
        
        ListMessagesResponse response = gmail.users().messages().list("me").setMaxResults((long) count).setPageToken(start).setQ(query.toString()).execute();
        
        List<MailInfo> mails = new ArrayList();
        List<Message> messages = response.getMessages();
        
        if(messages != null){
            for (Message message : messages) {
                MailInfo info = buildMailInfo(message.getId());
                mails.add(info);
            }
        }
        
        return new Pair<String, List<MailInfo>>(response.getNextPageToken(), mails);
    }

    
    /**
     * get mail detail
     * @param user
     * @param emailId
     * @return
     * @throws Exception
     */
    public MailInfo getEmail(String messageId) throws Exception {
        return this.buildMailInfo(messageId);
    }

    public MailInfo buildMailInfo(String messageId) {
        
        Message message = null;
        try {
            message = getGmailClient().users().messages().get("me", messageId).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        MailInfo mailInfo = new MailInfo();
        mailInfo.setContent(message.getSnippet());
        mailInfo.setId(messageId);
        List<MessagePartHeader> headers = message.getPayload().getHeaders();
        for(MessagePartHeader header :headers){
            if(header.getName().equals("Date")){
                mailInfo.setDate(header.getValue());
            }
            if(header.getName().equals("Subject")){
                mailInfo.setSubject(header.getValue());
            }
            if(header.getName().equals("From")){
                mailInfo.setFrom(header.getValue());
            }
        }
    	return mailInfo;
    }
    
    public boolean sendMail(String subject, String content, String to) throws Exception {
        String email = authService.getSocialIdEntity().getEmail();
        Gmail gmail = getGmailClient();
        Session session = null;
        MimeMessage msg = new MimeMessage(session);
        try {
            msg.setFrom(new InternetAddress(email));
            msg.setSubject(subject);
            msg.setContent(content, "text/html;charset=UTF-8");
            InternetAddress[] iaRecevers = new InternetAddress[1];
            iaRecevers[0] = new InternetAddress(to);
            msg.setRecipients(javax.mail.Message.RecipientType.TO, iaRecevers);
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            msg.writeTo(baos);
            String encodedEmail = Base64.encodeBase64URLSafeString(baos.toByteArray());
            Message message = new Message();
            message.setRaw(encodedEmail);
            
            gmail.users().messages().send("me", message).execute();
            
            return true;
        } catch (Exception e) {
            return false;
        }

    }
    
    /**
     * delete mail
     * @param user
     * @param emailId
     * @throws Exception
     */
    public void deleteEmail(String messageId) {
        try {
            gmail.users().messages().delete("me", messageId).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    

    private Gmail getGmailClient(){
        if(gmail == null){
            HttpTransport httpTransport = new NetHttpTransport();
            JacksonFactory jsonFactory = new JacksonFactory();
            GoogleCredential credential = new GoogleCredential().setAccessToken(authService.getSocialIdEntity().getToken());
            gmail = new Gmail.Builder(httpTransport, jsonFactory, credential).setApplicationName("Gmail Test").build();
        }
        return gmail;
    }

}
