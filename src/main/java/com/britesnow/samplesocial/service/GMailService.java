package com.britesnow.samplesocial.service;

import com.britesnow.samplesocial.entity.SocialIdEntity;
import com.britesnow.samplesocial.entity.User;
import com.britesnow.samplesocial.mail.OAuth2Authenticator;
import com.britesnow.snow.util.Pair;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sun.mail.imap.IMAPStore;

import javax.mail.FetchProfile;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.search.FromStringTerm;
import javax.mail.search.OrTerm;
import javax.mail.search.SearchTerm;
import javax.mail.search.SubjectTerm;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class GMailService {
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
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {
            if (inbox != null)
                inbox.close(true);
        }
        return null;
    }

    public void sendMail(User user, String subject, String content, String to) throws Exception {
//        SMTPTransport transport = emailAuthenticator.connectToSmtp(email, token);
//        Session mailSession = emailAuthenticator.getSMTPSession(token);
//        Message msg = new MimeMessage(mailSession);
//        try {
//            msg.setFrom(new InternetAddress(email));
//            msg.setSubject(subject);
//            msg.setContent(content, "text/html;charset=UTF-8");
//            InternetAddress[] iaRecevers = new InternetAddress[1];
//            iaRecevers[0] = new InternetAddress(to);
//            msg.setRecipients(Message.RecipientType.TO, iaRecevers);
//            transport.sendMessage(msg, msg.getAllRecipients());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
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
