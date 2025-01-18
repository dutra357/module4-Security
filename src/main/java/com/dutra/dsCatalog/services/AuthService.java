package com.dutra.dsCatalog.services;

import com.dutra.dsCatalog.dtos.EmailDto;
import com.dutra.dsCatalog.services.email.EmailService;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final EmailService emailService;
    public AuthService(EmailService emailService) {
        this.emailService = emailService;
    }

    public void createRecoverToken(EmailDto email) {

    }
}
