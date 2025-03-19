package com.arjun.services;
import com.arjun.models.Email;
import com.arjun.repositories.EmailRepository;
import com.sun.mail.imap.IMAPFolder;
import lombok.RequiredArgsConstructor;
import org.apache.commons.math3.stat.descriptive.moment.SemiVariance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

@Service
@RequiredArgsConstructor
public class EmailService {

    @Value("${mail.host}")
    private String host;

    @Value("${mail.port}")
    private int port;

    @Value("${mail.username}")
    private String username;

    @Value("${mail.password}")
    private String password;

    private final EmailRepository emailRepository;

    public void fetchAndSaveEmails() {
        try {
            Properties properties = new Properties();
            properties.put("mail.store.protocol", "imaps");

            Session session = Session.getInstance(properties);
            Store store = session.getStore("imaps");
            store.connect(host, username, password);

            IMAPFolder inbox = (IMAPFolder) store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            Message[] messages = inbox.getMessages();

            for (Message message : messages) {
                String sender = ((InternetAddress) message.getFrom()[0]).getAddress();
                String recipient = ((InternetAddress) message.getAllRecipients()[0]).getAddress();
                String subject = message.getSubject();
                Date receivedDate = message.getReceivedDate();

                // Check if email already exists
                boolean exists = emailRepository.existsBySenderAndSubjectAndReceivedDate(sender, subject, receivedDate);
                if (!exists) {
                    Email email = new Email();
                    email.setSender(sender);
                    email.setRecipient(recipient);
                    email.setSubject(subject);
                    email.setReceivedDate(receivedDate);

                    // Fetch and store body
                    email.setBody(getTextFromMessage(message));

                    // Save to MongoDB
                    emailRepository.save(email);
                }
            }
            inbox.close();
            store.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getTextFromMessage(Message message) throws MessagingException, IOException {

        if(message.isMimeType("text/plain")){
            return message.getContent().toString();
        }else if(message.isMimeType("multipart/*")){
            MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
            return getTextFromMimeMultipart(mimeMultipart);
        }
        return "";
    }
    private String getTextFromMimeMultipart(MimeMultipart mimeMultipart) throws MessagingException, IOException {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < mimeMultipart.getCount(); i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                result.append(bodyPart.getContent());
            } else if (bodyPart.getContent() instanceof MimeMultipart) {
                result.append(getTextFromMimeMultipart((MimeMultipart) bodyPart.getContent()));
            }
        }
        return result.toString();
    }

}
