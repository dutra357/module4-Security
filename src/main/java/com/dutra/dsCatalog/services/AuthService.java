package com.dutra.dsCatalog.services;

import com.dutra.dsCatalog.dtos.EmailDto;
import com.dutra.dsCatalog.dtos.NewPasswordDto;
import com.dutra.dsCatalog.entities.PasswordRecover;
import com.dutra.dsCatalog.entities.User;
import com.dutra.dsCatalog.repositories.PasswordRecoveryRepository;
import com.dutra.dsCatalog.repositories.UserRepository;
import com.dutra.dsCatalog.services.email.EmailService;
import com.dutra.dsCatalog.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
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
    private final PasswordEncoder passwordEncoder;
    public AuthService(EmailService emailService, PasswordRecoveryRepository passwordRecoveryRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.emailService = emailService;
        this.passwordRecoveryRepository = passwordRecoveryRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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

        String msg = "Link to recovery password:\n\n" + linkRecovery + token;
        emailService.sendMail(email.getEmail(), "Password Recovery", msg);
    }

    @Transactional
    public void saveNewPassword(NewPasswordDto entity) {
        List<PasswordRecover> results = passwordRecoveryRepository.searchValidTokens(entity.getToken(), Instant.now());
        if (results.size() == 0) {
            throw new ResourceNotFoundException("Invalid token recovery.");
        }

        User user = userRepository.findByEmail(results.get(0).getEmail());
        user.setPassword(passwordEncoder.encode(entity.getPassword()));
        userRepository.save(user);
    }
}
