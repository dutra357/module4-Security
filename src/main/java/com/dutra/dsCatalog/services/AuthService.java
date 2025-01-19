package com.dutra.dsCatalog.services;

import com.dutra.dsCatalog.dtos.EmailDto;
import com.dutra.dsCatalog.entities.PasswordRecover;
import com.dutra.dsCatalog.repositories.PasswordRecoveryRepository;
import com.dutra.dsCatalog.repositories.UserRepository;
import com.dutra.dsCatalog.services.email.EmailService;
import com.dutra.dsCatalog.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class AuthService {

    @Value("${email.password-recover.token.minutes}")
    private Long tokenMinutes;
    @Value("${email.password-recover.uri}")
    private String linkRecovery;

    private final EmailService emailService;
    private final PasswordRecoveryRepository passwordRecoveryRepository;
    private final UserRepository userRepository;
    public AuthService(EmailService emailService, PasswordRecoveryRepository passwordRecoveryRepository, UserRepository userRepository) {
        this.emailService = emailService;
        this.passwordRecoveryRepository = passwordRecoveryRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void createRecoverToken(EmailDto email) {
        if(userRepository.findByEmail(email.getEmail()) == null) {
            throw new ResourceNotFoundException("E-mail not found.");
        }

        PasswordRecover passwordRecover = new PasswordRecover();

        passwordRecover.setEmail(email.getEmail());

        String token = UUID.randomUUID().toString();
        passwordRecover.setToken(token);
        passwordRecover.setExpiration(Instant.now().plusSeconds(tokenMinutes * 60));

        passwordRecoveryRepository.save(passwordRecover);

        String msg = "Acesso o link para alterar sua senha.\n\n" + linkRecovery + token;

        emailService.sendMail(email.getEmail(), "Password Recovery", msg);
    }
}
