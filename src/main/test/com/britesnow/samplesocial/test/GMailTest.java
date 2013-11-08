package com.britesnow.samplesocial.test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import com.sun.mail.imap.IMAPStore;

public class GMailTest extends SnowTestSupport {
    @BeforeClass
    public static void initTestClass() throws Exception {
        SnowTestSupport.initWebApplication("src/main/webapp");
    }

    @Test
    public void testSearch() {
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

                SubjectTerm subjectTerm = new SubjectTerm("2013-11-07");
                BodyTerm bodyTerm = new BodyTerm("liu");
                FromStringTerm formTerm = new FromStringTerm("jeremy");
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                Date d = df.parse("2013-11-07");
                ReceivedDateTerm dateTerm = new ReceivedDateTerm(DateTerm.GT,d);
                AndTerm andTerm = new AndTerm(new SearchTerm[]{bodyTerm,subjectTerm,formTerm,dateTerm});
                Message[] messages = inbox.search(andTerm);
                
                for(Message message : messages){
                    System.out.println(message.getSubject() +"-------"+ message.getFrom()[0]);
                }

                System.out.println("finished");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
