package com.britesnow.samplesocial.service;

import com.britesnow.samplesocial.entity.SocialIdEntity;
import com.britesnow.samplesocial.entity.User;
import com.britesnow.samplesocial.mail.MailInfo;
import com.britesnow.samplesocial.mail.OAuth2Authenticator;
import com.britesnow.samplesocial.oauth.OauthException;
import com.britesnow.snow.util.Pair;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sun.mail.imap.IMAPStore;
import com.sun.mail.smtp.SMTPTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.FetchProfile;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import javax.mail.search.FromStringTerm;
import javax.mail.search.OrTerm;
import javax.mail.search.SearchTerm;
import javax.mail.search.SubjectTerm;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Singleton
public class GMailService {
    private static Logger log = LoggerFactory.getLogger(GMailService.class);
    @Inject
    OAuth2Authenticator emailAuthenticator;

    @Inject
    GoogleAuthService authService;

    /**
     * list mails
     * @param user oauth user
     * @param folderName   foldr name
     * @param start   start
     * @param count   count
     * @return  pare of couf and messages
     * @throws Exception
     */
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

    /**
     * list folder
     * @param user
     * @return
     * @throws Exception
     */
    public Folder[] listFolders(User user) throws Exception {
        IMAPStore imap = getImapStore(user);
        return imap.getDefaultFolder().list();
    }
    public Folder getFolder(User user, String folderName) throws Exception {
        IMAPStore imap = getImapStore(user);
        return imap.getFolder(folderName);
    }

    /**
     * get mail detail
     * @param user
     * @param emailId
     * @return
     * @throws Exception
     */
    public MailInfo getEmail(User user, int emailId) throws Exception {
        IMAPStore imap = getImapStore(user);
        Folder inbox = imap.getFolder("INBOX");
        if (!inbox.isOpen()) {
            inbox.open(Folder.READ_ONLY);
        }

        Message message = inbox.getMessage(emailId);
        MailInfo info = buildMailInfo(message);
        info.setContent(getContent(message));
        return info;
    }

    /**
     * delete mail
     * @param user
     * @param emailId
     * @throws Exception
     */
    public void deleteEmail(User user, int emailId) throws Exception {
        IMAPStore imap = getImapStore(user);
        Folder inbox = imap.getFolder("INBOX");

        inbox.open(Folder.READ_WRITE);
        Message msg = inbox.getMessage(emailId);
        msg.setFlag(Flags.Flag.DELETED, true);
    }

    /**
     * delete folder
     * @param user
     * @param folderName
     * @return  delete ok or not
     * @throws Exception
     */
    public boolean deleteFolder(User user, String folderName) throws Exception {
        IMAPStore imap = getImapStore(user);
        Folder folder = imap.getFolder(folderName);
        if (folder.isOpen()) {
            folder.close(true);
        }
        return folder.delete(true);
    }

    /**
     * search mail
     * @param user  auth user
     * @param subject search object
     * @param from    search from
     * @param pageSize  page size
     * @param pageIndex  page index
     * @return   pair of count and mail info.
     * @throws Exception
     */
    public Pair<List<MailInfo>, Integer> search(User user, String subject, String from, int pageSize, int pageIndex) throws Exception {
        Folder inbox = null;
        List<MailInfo> infos = new ArrayList<MailInfo>();
        int count = 0;
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
                Message[] msgs =  inbox.search(new OrTerm(searchTerms.toArray(new SearchTerm[searchTerms.size()])));
                count = msgs.length;
                Message[] resultMsgs={};
                int start = pageSize*pageIndex;
                if (msgs.length > start && msgs.length > start+pageSize) {
                    resultMsgs = Arrays.copyOfRange(msgs, start, start + pageSize);
                }else if (msgs.length > start && msgs.length < start + pageSize) {
                    resultMsgs = Arrays.copyOfRange(msgs, start, msgs.length);
                }

                if (resultMsgs.length > 0) {
                    for (Message msg : resultMsgs) {
                        infos.add(buildMailInfo(msg));
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inbox != null)
                inbox.close(true);
        }
        return new Pair<List<MailInfo>, Integer>(infos, count);
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
                Multipart multipart = (Multipart) part.getBodyPart(0).getContent();
                str.append(multipart.getBodyPart(1).getContent());
            }
        }
        return str.toString();
    }

    public MailInfo buildMailInfo(Message message) throws MessagingException, UnsupportedEncodingException {
        return new MailInfo(message.getMessageNumber(), message.getSentDate().getTime(),
                decodeText(message.getFrom()[0].toString()), message.getSubject());
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


}
