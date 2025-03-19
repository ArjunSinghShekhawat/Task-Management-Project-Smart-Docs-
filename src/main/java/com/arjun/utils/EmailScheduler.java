package com.arjun.utils;


import com.arjun.services.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailScheduler {

    private final EmailService emailService;

    @Scheduled(fixedRate = 60000)
    public void fetchEmails() {
        System.out.println("I am Invoke !");
        emailService.fetchAndSaveEmails();
    }
}
