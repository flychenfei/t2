package com.britesnow.samplesocial.service;

import com.britesnow.samplesocial.entity.SocialIdEntity;
import com.britesnow.samplesocial.entity.User;
import com.britesnow.samplesocial.mail.OAuth2Authenticator;
import com.britesnow.samplesocial.oauth.OauthException;
import com.britesnow.snow.util.Pair;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sun.mail.imap.IMAPStore;
import com.sun.mail.smtp.SMTPTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.search.FromStringTerm;
import javax.mail.search.OrTerm;
import javax.mail.search.SearchTerm;
import javax.mail.search.SubjectTerm;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class GMailService {
    private static Logger log = LoggerFactory.getLogger(GMailService.class);
    @Inject
    OAuth2Authenticator emailAuthenticator;

    @Inject
    GoogleAuthService authService;

    public Pair<Integer, Message[]> listMails(User user, String folderName, int start, int count) throws Exception {
        IMAPStore imap = getImapStore(user);

        Folder inbox;
        if (folderName == null) {
            inbox = imap.getFolder("INBOX");
        } else {
            inbox = imap.getFolder(folderName);
        }

        inbox.open(Folder.READ_ONLY);
        FetchProfile profile = new FetchProfile();
        profile.add(FetchProfile.Item.ENVELOPE);
        if (!inbox.isOpen()) {
            inbox.open(Folder.READ_ONLY);
        }
        int total = inbox.getMessageCount();
        if (total > 0) {
            if (total - start - count > 0) {
                start = total - count - start;
                count --;
            } else {
                if (total - start > 0) {
                    start = total - start;
                    count = total - start;
                } else {
                    start = 1;
                    count = total - start;
                }
            }
            System.out.println(String.format("start %s count %s", start, count));
            return new Pair<Integer, Message[]>(total, inbox.getMessages(start, start + count));
        }
        return new Pair<Integer, Message[]>(0, new Message[0]);
    }


    public Folder[] listFolders(User user) throws Exception {
        IMAPStore imap = getImapStore(user);
        return imap.getDefaultFolder().list();
    }
    public Folder getFolder(User user, String folderName) throws Exception {
        IMAPStore imap = getImapStore(user);
        return imap.getFolder(folderName);
    }

    public Message getEmail(User user, int emailId) throws Exception {
        IMAPStore imap = getImapStore(user);
        Folder inbox = imap.getFolder("INBOX");
        inbox.open(Folder.READ_ONLY);
        FetchProfile profile = new FetchProfile();
        profile.add(FetchProfile.Item.ENVELOPE);
        if (!inbox.isOpen()) {
            inbox.open(Folder.READ_ONLY);
        }

        Message message = inbox.getMessage(emailId);
        return message;
    }

    public void deleteEmail(User user, int emailId) throws Exception {
        IMAPStore imap = getImapStore(user);
        Folder inbox = imap.getFolder("INBOX");

        inbox.open(Folder.READ_WRITE);
        Message msg = inbox.getMessage(emailId);
        msg.setFlag(Flags.Flag.DELETED, true);
    }
    public boolean deleteFolder(User user, String folderName) throws Exception {
        IMAPStore imap = getImapStore(user);
        Folder folder = imap.getFolder(folderName);
        if (folder.isOpen()) {
            folder.close(true);
        }
        return folder.delete(true);
    }

    public Message[] search(User user, String subject, String from) throws Exception {
        Folder inbox = null;
        try {
            IMAPStore imap = getImapStore(user);

            inbox = imap.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);
            List<SearchTerm> searchTerms = new ArrayList<SearchTerm>();
            if (subject != null) {
                SubjectTerm subjectTerm = new SubjectTerm(subject);
                searchTerms.add(subjectTerm);
            }
            if (from != null) {
                FromStringTerm fromStringTerm = new FromStringTerm(from);
                searchTerms.add(fromStringTerm);
            }
            if (searchTerms.size() > 0) {
                return inbox.search(new OrTerm(searchTerms.toArray(new SearchTerm[searchTerms.size()])));


            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inbox != null)
                inbox.close(true);
        }
        return null;
    }

    public boolean sendMail(User user, String subject, String content, String to) throws Exception {
        SocialIdEntity idEntity = authService.getSocialIdEntity(user.getId());
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

    private IMAPStore getImapStore(User user) throws Exception {
        if (user != null) {
            SocialIdEntity social = authService.getSocialIdEntity(user.getId());
            if (social != null && social.getEmail() != null && social.isValid()) {
                return emailAuthenticator.connectToImap(social.getEmail(), social.getToken());
            }
        }
        throw new IllegalArgumentException("access token is invalid");
    }


}
