package com.arjun.services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailFetchService {

    @Autowired
    private JavaMailSender javaMailSender;

    public boolean sendMail(String to,String subject,String body) {
        boolean isSend=false;
        try{
            System.out.println("Come email "+to);
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(to);
            mailMessage.setSubject(subject);
            mailMessage.setText(body);

            this.javaMailSender.send(mailMessage);
            isSend=true;

            return isSend;

        }catch (Exception e){
          e.printStackTrace();
            return isSend;
        }
    }
}
