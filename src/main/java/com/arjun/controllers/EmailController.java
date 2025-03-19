package com.arjun.controllers;

import com.arjun.models.Email;
import com.arjun.repositories.EmailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/auth/emails")
@RequiredArgsConstructor
public class EmailController {

    private final EmailRepository emailRepository;

    @GetMapping
    public List<Email> getAllEmails() {
        return emailRepository.findAll();
    }
}
