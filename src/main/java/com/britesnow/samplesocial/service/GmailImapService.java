package com.britesnow.samplesocial.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.britesnow.samplesocial.mail.MailInfo;
import com.britesnow.samplesocial.mail.OAuth2Authenticator;
import com.britesnow.samplesocial.model.SocialIdEntity;
import com.britesnow.samplesocial.oauth.OauthException;
import com.britesnow.snow.util.Pair;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sun.mail.gimap.GmailRawSearchTerm;
import com.sun.mail.gimap.GmailStore;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;
import com.sun.mail.smtp.SMTPTransport;

@Singleton
public class GmailImapService {
    private static Logger log = LoggerFactory.getLogger(GmailImapService.class);
    @Inject
    OAuth2Authenticator emailAuthenticator;

    @Inject
    GoogleAuthService authService;

    public Pair<Integer, List<MailInfo>> search(String subject, String from, String to, 
    		String body, String sDate , String eDate, String srDate , String erDate,
    		String label, String hasAttachment , String attachmentName , String cc ,
    		String list, String hasCircle , String circle , String chatContent ,
    		String unread,String category , String deliveredTo , String rfc822msgid ,
    		Integer minSize, Integer maxSize, int start, int count) throws Exception  {
    	IMAPStore imap = getGmailStore();
    	String folderName = getGmailFolderName(imap,"\\All");
    	Folder inbox = imap.getFolder(folderName);
        inbox.open(Folder.READ_ONLY);
    
        StringBuffer searchTerms = new StringBuffer();
        
        if (subject != null) {
        	searchTerms.append("subject:");
        	searchTerms.append(subject);
        }
        if (from != null) {
        	searchTerms.append(" from:");
        	searchTerms.append(from);
        }
        if (to != null) {
        	searchTerms.append(" to:");
        	searchTerms.append(to);
        }
        if (body != null) {
        	searchTerms.append(" \"");
        	searchTerms.append(body);
        	searchTerms.append("\"");
        }
        if (sDate != null) {
        	searchTerms.append(" after:");
        	searchTerms.append(sDate);
        }
        if (eDate != null) {
        	searchTerms.append(" before:");
        	searchTerms.append(eDate);
        }
        if (srDate != null) {
        	searchTerms.append(" after:");
        	searchTerms.append(srDate);
        }
        if (erDate != null) {
        	searchTerms.append(" before:");
        	searchTerms.append(erDate);
        }
        if (label != null) {
        	searchTerms.append(" label:");
        	searchTerms.append(label);
        }
        if (hasAttachment !=null) {
        	searchTerms.append(" has:attachment");
        }
        if (attachmentName != null) {
        	searchTerms.append(" filename:");
        	searchTerms.append(attachmentName);
        }
        if (cc != null) {
        	searchTerms.append(" cc:");
        	searchTerms.append(cc);
        }
        if (minSize != null) {
        	searchTerms.append(" larger:");
        	searchTerms.append(minSize.toString());
        }
        if (maxSize != null) {
        	searchTerms.append(" smaller:");
        	searchTerms.append(maxSize.toString());
        }
        if (list != null) {
        	searchTerms.append(" list:");
        	searchTerms.append(list);
        }
        if (hasCircle != null) {
        	searchTerms.append(" has:circle");
        }
        if (circle != null) {
        	searchTerms.append(" circle:");
        	searchTerms.append(circle);
        }
        if (chatContent != null) {
        	searchTerms.append(" is:chat ");
        	searchTerms.append(chatContent);
        }
        if (unread != null) {
        	searchTerms.append(" is:unread");
        }
        if (category != null) {
        	searchTerms.append(" category:");
        	searchTerms.append(category);
        }
        if (deliveredTo != null) {
        	searchTerms.append(" deliveredTo:");
        	searchTerms.append(deliveredTo);
        }
        if (rfc822msgid != null) {
        	searchTerms.append(" rfc822msgid:");
        	searchTerms.append(rfc822msgid);
        }
        
        GmailRawSearchTerm gmailSearchTerm = new GmailRawSearchTerm(searchTerms.toString());
        
        List<MailInfo> mails = new ArrayList();
        Message[] messages = null;
        
    	int total = 0;
        if (gmailSearchTerm != null) {
        	Message[] msgs = inbox.search(gmailSearchTerm);
            total = msgs.length;
    
            if (total > 0) {
                Integer end = getEnd(start, count, total);
                if (end != null) {
                    int finalStart = total + 1 - end;
                    int finalEnd = total + 1 - start;
                    
                    messages = new Message[finalEnd - finalStart + 1];
                    for (int i = 0; i < messages.length; i++) {
                        messages[i] = msgs[finalStart + i - 1];
                    }
                }
            }
        }
        if(messages != null){
            for (Message message : messages) {
                MailInfo info = buildMailInfo(message);
                mails.add(0, info);
            }
        }
        if(inbox.isOpen()){
            inbox.close(true);
        }
        imap.close();
        return new Pair<Integer, List<MailInfo>>(total, mails);
    }

    /**
     * list folder
     * @param user
     * @return
     * @throws Exception
     */
    public Folder[] listFolders() throws Exception {
        IMAPStore imap = getGmailStore();
        Folder[] folders = imap.getDefaultFolder().list("*");
        imap.close();
        return folders;
    }
    
    public Folder getFolder(String folderName) throws Exception {
        IMAPStore imap = getGmailStore();
        return imap.getFolder(folderName);
    }
    
    /**
     * For gmail folders,
     * LIST (\HasNoChildren) "/" "INBOX"
     * LIST (\Noselect \HasChildren) "/" "[Gmail]"
     * LIST (\HasNoChildren \All) "/" "[Gmail]/All Mail"
     * LIST (\HasNoChildren \Drafts) "/" "[Gmail]/Drafts"
     * LIST (\HasNoChildren \Important) "/" "[Gmail]/Important"
     * LIST (\HasNoChildren \Sent) "/" "[Gmail]/Sent Mail"
     * LIST (\HasNoChildren \Junk) "/" "[Gmail]/Spam"
     * LIST (\HasNoChildren \Flagged) "/" "[Gmail]/Starred"
     * LIST (\HasNoChildren \Trash) "/" "[Gmail]/Trash"
     * LIST (\HasNoChildren) "/" "[Gmail]Trash"
     * @param attribute
     * @return
     * @throws Exception
     */
    public String getGmailFolderName(IMAPStore imap, String attribute) throws Exception {
        Folder[] folders = imap.getDefaultFolder().list("*");
        String folder = null;
        for (Folder f : folders) {
            IMAPFolder imapFolder = (IMAPFolder) f;
            for(String attr : imapFolder.getAttributes()) {
                if (attribute != null && attribute.equals(attr)) {
                    folder = f.getFullName();
                }
            }
        }
        if(folder != null){
            return folder;
        }
        return null;
    }

    /**
     * get mail detail
     * @param user
     * @param emailId
     * @return
     * @throws Exception
     */
    public MailInfo getEmail(int emailId) throws Exception {
        IMAPStore imap = getGmailStore();
        String folderName = getGmailFolderName(imap,"\\All");
        Folder inbox = imap.getFolder(folderName);
        if (!inbox.isOpen()) {
            inbox.open(Folder.READ_ONLY);
        }

        Message message = inbox.getMessage(emailId);
        MailInfo info = buildMailInfo(message);
        
        try {
            if(message.getContent() != null){
                List attachments = new ArrayList();
                StringBuffer str = new StringBuffer();
                if (message.isMimeType("text/plain") || message.getContent() instanceof String){
                    str.append(message.getContent().toString());
                }else{
                    Multipart multiPart = (Multipart) message.getContent();
                    if (message.isMimeType("multipart/alternative")) {
                        str.append(multiPart.getBodyPart(1).getContent().toString());
                    }else if (message.isMimeType("multipart/related")) {
                        Multipart cpart = (Multipart) multiPart.getBodyPart(0).getContent();
                        str.append(cpart.getBodyPart(1).getContent().toString());
                    }else if (message.isMimeType("multipart/mixed")) {
                        if (multiPart.getBodyPart(0).isMimeType("text/plain")) {
                            str.append(multiPart.getBodyPart(0).getContent());
                        }
                        if (multiPart.getBodyPart(0).isMimeType("multipart/alternative")) {
                            Multipart multipart = (Multipart) multiPart.getBodyPart(0).getContent();
                            str.append(multipart.getBodyPart(1).getContent());
                        }
                    }
                    
                    int id = 1;
                    for (int i = 0; i < multiPart.getCount(); i++) {
                        BodyPart bodyPart = multiPart.getBodyPart(i);
                        if(!Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition()) && !StringUtils.isNotBlank(bodyPart.getFileName())) {
                          continue; 
                        }
                        Map map = new HashMap();
                        map.put("id", id);
                        map.put("messageId", info.getId());
                        map.put("name", bodyPart.getFileName());
                        map.put("type", bodyPart.getContentType());
                        attachments.add(map);
                        id++;
                        
                    }
                    
                    info.setAttachments(attachments);
                }
                info.setContent(str.toString());
                
                
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return info;
    }
    
    public InputStream getAttachment(Integer emailId, Integer attachmentId) throws Exception {
        IMAPStore imap = getGmailStore();
        String folderName = getGmailFolderName(imap,"\\All");
        Folder inbox = imap.getFolder(folderName);
        if (!inbox.isOpen()) {
            inbox.open(Folder.READ_ONLY);
        }

        Message message = inbox.getMessage(emailId);
        
        try {
            if (message.getContent() != null) {
                Multipart multiPart = (Multipart) message.getContent();
                int id = 1;
                for (int i = 0; i < multiPart.getCount(); i++) {
                    BodyPart bodyPart = multiPart.getBodyPart(i);
                    if (!Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition()) && !StringUtils.isNotBlank(bodyPart.getFileName())) {
                        continue;
                    }
                    if(attachmentId == id){
                        return bodyPart.getInputStream();
                    }
                    id++;
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        imap.close();
        return null;
    }

    /**
     * delete mail
     * @param user
     * @param emailId
     * @throws Exception
     */
    public void deleteEmail(int emailId) throws Exception {
        IMAPStore imap = getGmailStore();
        String folderName = getGmailFolderName(imap,"\\All");
        Folder inbox = imap.getFolder(folderName);

        inbox.open(Folder.READ_WRITE);
        Message msg = inbox.getMessage(emailId);
        msg.setFlag(Flags.Flag.DELETED, true);
        inbox.close(true);
        imap.close();
    }
    
    public void trashEmail(int emailId) throws Exception {
        IMAPStore imap = getGmailStore();
        String folderName = getGmailFolderName(imap,"\\All");
        Folder inbox = imap.getFolder(folderName);

        inbox.open(Folder.READ_WRITE);
        Message msg = inbox.getMessage(emailId);
        String trashName = getGmailFolderName(imap,"\\Trash");
        Folder trashFolder = imap.getFolder(trashName);
        
        // Just do copy, not do delete, the copy seems mean that move message
        inbox.copyMessages(new Message[]{msg}, trashFolder);

        inbox.close(false);
        imap.close();
    }

    /**
     * delete folder
     * @param user
     * @param folderName
     * @return  delete ok or not
     * @throws Exception
     */
    public boolean deleteFolder(String folderName) throws Exception {
        IMAPStore imap = getGmailStore();
        Folder folder = imap.getFolder(folderName);
        if (folder.isOpen()) {
            folder.close(true);
        }
        boolean success = folder.delete(true);
        imap.close();
        return success;
    }
    
    
    public boolean saveFolder(String folderName) throws Exception {
        GmailStore imap = getGmailStore();
        Folder folder = null;
        folder = imap.getFolder(folderName);
        if (!folder.exists()) {
            folder.create(Folder.HOLDS_MESSAGES);
        }
        imap.close();
        return true;
    }


    /**
     * sent mail
     * @param user  auth user
     * @param subject  mail subject
     * @param content  mail content
     * @param to  mail to
     * @return  mail ok or not
     * @throws Exception
     */
    public boolean sendMail(String subject, String content, String to) throws Exception {
        SocialIdEntity idEntity = authService.getSocialIdEntity();
        if (idEntity != null) {
            String email = idEntity.getEmail();
            String token = idEntity.getToken();
            SMTPTransport transport = emailAuthenticator.connectToSmtp(email, token);
            Session mailSession = emailAuthenticator.getSMTPSession(token);
            Message msg = new MimeMessage(mailSession);
            try {
                msg.setFrom(new InternetAddress(email));
                msg.setSubject(subject);
                msg.setContent(content, "text/html;charset=UTF-8");
                InternetAddress[] iaRecevers = new InternetAddress[1];
                iaRecevers[0] = new InternetAddress(to);
                msg.setRecipients(Message.RecipientType.TO, iaRecevers);
                transport.sendMessage(msg, msg.getAllRecipients());
                return true;
            } catch (Exception e) {
                log.warn(String.format("send mail from %s fail", email), e);
                return false;
            }
        }else {
            throw new OauthException(authService.getAuthorizationUrl());
        }

    }

    public MailInfo buildMailInfo(Message message) throws MessagingException, UnsupportedEncodingException {
        MailInfo mailInfo = new MailInfo(message.getMessageNumber(), message.getSentDate().getTime(),
            decodeText(message.getFrom()[0].toString()), message.getSubject());
    	return mailInfo;
    }

    private GmailStore getGmailStore() throws Exception {
        SocialIdEntity social = authService.getSocialIdEntity();
        if(social != null && social.getEmail() != null){
            return emailAuthenticator.connectToGmailImap(social.getEmail(), social.getToken());
        }
        throw new IllegalArgumentException("access token is invalid");
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
    
    private Integer getEnd(int start, int count, int total){
        //prerequire total should be > 0
        Integer end = null;
        if(start > total){
            return null;
        }else if(start > total - count){
            end = total;
        }else{
            end = start + count - 1;
        }
        return end;
    }
}
