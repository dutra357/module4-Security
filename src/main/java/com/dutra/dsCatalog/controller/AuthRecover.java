package com.dutra.dsCatalog.controller;

import com.dutra.dsCatalog.dtos.EmailDto;
import com.dutra.dsCatalog.services.AuthService;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/auth")
public class AuthRecover {

    private final AuthService emailService;
    public AuthRecover(AuthService emailService) {
        this.emailService = emailService;
    }

    @PostMapping(value = "/recover-token")
    public ResponseEntity<Void> createRecoverToken(@Valid @RequestBody EmailDto body) {
        emailService.createRecoverToken(body);
        return ResponseEntity.noContent().build();
    }
}
