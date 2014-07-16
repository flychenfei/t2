package com.britesnow.samplesocial.service;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.britesnow.samplesocial.mail.MailInfo;
import com.britesnow.snow.util.Pair;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Label;
import com.google.api.services.gmail.model.ListLabelsResponse;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartBody;
import com.google.api.services.gmail.model.MessagePartHeader;
import com.google.api.services.gmail.model.Thread;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class GmailRestService {
    private static Logger log = LoggerFactory.getLogger(GmailRestService.class);
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
        
        final List<MailInfo> mails = new ArrayList();
        List<Message> messages = response.getMessages();
        
        if(messages != null){
            
            BatchRequest batch = gmail.batch();
            JsonBatchCallback<Message> callback = new JsonBatchCallback<Message>() {

                public void onSuccess(Message message, HttpHeaders responseHeaders) {
                    MailInfo info = buildMailInfo(message);
                    mails.add(info);
                }

                public void onFailure(GoogleJsonError e, HttpHeaders responseHeaders) {
                    log.warn("Error Message: " + e.getMessage());
                }
            };

            for (Message message : messages) {
                getGmailClient().users().messages().get("me", message.getId()).queue(batch, callback);
            }
            
            batch.execute();
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
        Message message = null;
        try {
            message = getGmailClient().users().messages().get("me", messageId).execute();
            return buildMailInfo(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public MailInfo buildMailInfo(Message message) {
        
        MailInfo mailInfo = new MailInfo();
        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.US);
        
        mailInfo.setId(message.getId());
        mailInfo.setThreadId(message.getThreadId());
        if(message.getPayload() != null){
            
            if(message.getPayload().getHeaders() != null){
                for(MessagePartHeader header : message.getPayload().getHeaders()){
                    if(header.getName().equals("Subject")){
                        mailInfo.setSubject(header.getValue());
                    }
                    if(header.getName().equals("From")){
                        mailInfo.setFrom(header.getValue());
                    }
                    if(header.getName().equals("Date")){
                        try {
                            mailInfo.setDate(df.parse(String.valueOf(header.getValue())).getTime());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            
            List attachments = new ArrayList();
            String body = null;
            if(message.getPayload().getParts() != null){
                if(message.getPayload().getParts().size() == 1){
                    body = message.getPayload().getParts().get(0).getBody().getData();
                }else{
                    for(MessagePart part : message.getPayload().getParts()){
                        if(part.getMimeType().equalsIgnoreCase("text/html")){
                            body = part.getBody().getData();
                            mailInfo.setContent(getContent(body));
                        }else{
                            
                            if(part.getMimeType().equalsIgnoreCase("text/plain")){
                                body = part.getBody().getData();
                            }else if (part.getMimeType().equalsIgnoreCase("multipart/alternative")) {
                                body = part.getParts().get(1).getBody().getData();
                            }else if (part.getMimeType().equalsIgnoreCase("multipart/related")) {
                                body = part.getParts().get(0).getParts().get(1).getBody().getData();
                            }else if (part.getMimeType().equalsIgnoreCase("multipart/mixed")) {
                                if (part.getParts().get(0).getMimeType().equalsIgnoreCase("text/plain")) {
                                    body = part.getParts().get(0).getBody().getData();
                                }else if (part.getParts().get(0).getMimeType().equalsIgnoreCase("multipart/alternative")) {
                                    body = part.getParts().get(1).getBody().getData();
                                }
                            }
                            
                            
                            String attachmentId = part.getBody().getAttachmentId();
                            if(attachmentId != null && !attachmentId.equals("")){
                                Map map = new HashMap();
                                map.put("id", attachmentId);
                                map.put("messageId", mailInfo.getId());
                                map.put("name", part.getFilename());
                                map.put("type", part.getMimeType());
                                attachments.add(map);
                            }
                        }
                    }
                }
            }else{
                if(message.getPayload().getBody() != null){
                    body = message.getPayload().getBody().getData();
                }
            }
            if(body != null){
                mailInfo.setContent(getContent(body));
            }
            mailInfo.setAttachments(attachments);
            
        }
        
        
    	return mailInfo;
    }
    
    public boolean sendMail(String subject, String content, String to) throws Exception {
        String email = authService.getSocialIdEntity().getEmail();
        Gmail gmail = getGmailClient();
        Message message = new Message();
        
        StringBuilder raw = new StringBuilder();
        
        raw.append("Subject:");
        raw.append(subject);
        raw.append("\r\n");
        raw.append("From:");
        raw.append(email);
        raw.append("\r\n");
        raw.append("To:");
        raw.append(to);
        raw.append("\r\n");
        raw.append("\r\n");
        raw.append(content);
        raw.append("\r\n");
        
        String encodedEmail = Base64.encodeBase64URLSafeString(raw.toString().getBytes());
        message.setRaw(encodedEmail);
        
        gmail.users().messages().send("me", message).execute();
        return true;
    }
    
    /**
     * delete mail
     * @param user
     * @param emailId
     * @throws Exception
     */
    public void deleteEmail(String messageId) {
        try {
            getGmailClient().users().messages().delete("me", messageId).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void trashEmail(String messageId) {
        try {
            getGmailClient().users().messages().trash("me", messageId).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public List<Map> listLabels() throws Exception  {
        ListLabelsResponse response = getGmailClient().users().labels().list("me").execute();
        
        List<Map> labelsList = new ArrayList();
        List<Label> labels = response.getLabels();
        
        if(labels != null){
            
            for (Label label : labels) {
                Map labelInfo = new HashMap();
                labelInfo.put("id", label.getId());
                labelInfo.put("name", label.getName());
                labelInfo.put("type", label.getType());
                labelsList.add(labelInfo);
            }
            
        }
        
        return labelsList;
    }
    
    public void deleteLabel(String labelId) throws Exception  {
        try {
            getGmailClient().users().labels().delete("me", labelId).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public Map getLabel(String labelId) throws Exception  {
       Label label = getGmailClient().users().labels().get("me", labelId).execute();
       Map map  = new HashMap();
       map.put("id", label.getId());
       map.put("name", label.getName());
       map.put("type", label.getType());
       return map;
    }
    
    public void saveLabel(String labelId, String name) throws Exception  {
        Label label = new Label();
        label.setName(name);
        label.setMessageListVisibility("show");
        label.setLabelListVisibility("labelShow");
        if(labelId != null && !"".equals(labelId)){
            label.setId(labelId);
            getGmailClient().users().labels().update("me", labelId, label).execute();
        }else{
            getGmailClient().users().labels().create("me", label).execute();
        }
    }
    
    public List<MailInfo> getThreadMails(String threadId) throws Exception{
        Thread thread = getGmailClient().users().threads().get("me", threadId).execute();
        
        final List<MailInfo> mails = new ArrayList();
        List<Message> messages = thread.getMessages();
        
        if(messages != null){
            BatchRequest batch = gmail.batch();
            JsonBatchCallback<Message> callback = new JsonBatchCallback<Message>() {

                public void onSuccess(Message message, HttpHeaders responseHeaders) {
                    MailInfo info = buildMailInfo(message);
                    mails.add(info);
                }

                public void onFailure(GoogleJsonError e, HttpHeaders responseHeaders) {
                    log.warn("Error Message: " + e.getMessage());
                }
            };

            for (Message message : messages) {
                getGmailClient().users().messages().get("me", message.getId()).queue(batch, callback);
            }
            
            batch.execute();
        }
        
        return mails;
    }
    
    public byte[] getAttachment(String messageId, String attachmentId) throws Exception  {
        MessagePartBody attachPart = getGmailClient().users().messages().attachments().get("me", messageId, attachmentId).execute();
        byte[] fileByteArray = Base64.decodeBase64(attachPart.getData());
        return fileByteArray;
    }
    
    private String getContent(String body){
        
        body.replaceAll("-", "+");
        body.replaceAll("_", "/");
        body.replaceAll(",", "=");
        
        byte[] content = Base64.decodeBase64(body);
        return new String(content);
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
