package com.britesnow.samplesocial.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

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
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartHeader;
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
            
            if(message.getPayload().getParts() != null){
                for(MessagePart part : message.getPayload().getParts()){
                    if(part.getMimeType().equalsIgnoreCase("text/html")){
                        String body = part.getBody().getData();
                        
                        body.replaceAll("-", "+");
                        body.replaceAll("_", "/");
                        body.replaceAll(",", "=");
                        
                        byte[] content = Base64.decodeBase64(body);
                        mailInfo.setContent(new String(content));
                        break;
                    }
                }
            }
            
        }
        
        
    	return mailInfo;
    }
    
    public boolean sendMail(String subject, String content, String to) throws Exception {
        String email = authService.getSocialIdEntity().getEmail();
        Gmail gmail = getGmailClient();
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

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
