package com.britesnow.samplesocial.test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.search.AndTerm;
import javax.mail.search.BodyTerm;
import javax.mail.search.DateTerm;
import javax.mail.search.FromStringTerm;
import javax.mail.search.ReceivedDateTerm;
import javax.mail.search.SearchTerm;
import javax.mail.search.SubjectTerm;

import org.junit.BeforeClass;
import org.junit.Test;

import com.britesnow.snow.testsupport.SnowTestSupport;
import com.sun.mail.gimap.GmailRawSearchTerm;
import com.sun.mail.gimap.GmailSSLStore;
import com.sun.mail.imap.IMAPStore;

public class GMailTest extends SnowTestSupport {
    @BeforeClass
    public static void initTestClass() throws Exception {
    }

    @Test
    public void testSearch() {
        //Please Enter username and password
        String mail = "";
        String password = "";
        if (mail.length() > 0 && password.length() > 0) {
            try {
                Properties prop = System.getProperties();
                prop.put("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                prop.put("mail.imap.host", "imap.gmail.com");
                prop.put("mail.imap.port", "993");

                Session session = Session.getInstance(prop);
                session.setDebug(true);

                IMAPStore imap = (IMAPStore) session.getStore("imap");
                imap.connect(mail, password);

                Folder inbox = imap.getFolder("INBOX");
                inbox.open(Folder.READ_ONLY);

                List<SearchTerm> terms = new ArrayList<SearchTerm>();
                
                SubjectTerm subjectTerm = new SubjectTerm("2013-11-07");
                terms.add(subjectTerm);
                
                BodyTerm bodyTerm = new BodyTerm("liu");
                terms.add(bodyTerm);
                
                FromStringTerm fromTerm = new FromStringTerm("jeremy");
                terms.add(fromTerm);
                
//                SizeTerm maxSizeTerm = new SizeTerm(SizeTerm.GE, 6000);
//                terms.add(maxSizeTerm);
//                
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                Date d = df.parse("2013-11-07");
                ReceivedDateTerm dateTerm = new ReceivedDateTerm(DateTerm.GT,d);
                terms.add(dateTerm);
                
                AndTerm andTerm = new AndTerm(terms.toArray(new SearchTerm[0]));
                Message[] messages = inbox.search(andTerm);
                
//                for(Message m : messages){
//                    System.out.println(m.getSize());
//                }
                System.out.println(messages.length);
                System.out.println("finished");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
    
    @Test
    public void testGmailSearch() {
      //Please Enter username and password
        String mail = "";
        String password = "";
        if (mail.length() > 0 && password.length() > 0) {
            try {
                Properties prop = System.getProperties();
                prop.put("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                prop.put("mail.imap.host", "imap.gmail.com");
                prop.put("mail.imap.port", "993");
                prop.put("mail.debug", "true");

                Session session = Session.getInstance(prop);
                session.setDebug(true);

                GmailSSLStore imap = (GmailSSLStore) session.getStore("gimaps");
                imap.connect(mail, password);

                Folder inbox = imap.getFolder("INBOX");
                inbox.open(Folder.READ_ONLY);

                GmailRawSearchTerm gmailSearchTerm = new GmailRawSearchTerm("after:2013/09/01");
                
                Message[] messages = inbox.search(gmailSearchTerm);
                
//                for(Message m : messages){
//                    System.out.println(m.getSize());
//                }
                System.out.println(messages.length);
                System.out.println("finished");
            } catch (Exception e) {
                e.printStackTrace();
            }
            
        }
    }
}
