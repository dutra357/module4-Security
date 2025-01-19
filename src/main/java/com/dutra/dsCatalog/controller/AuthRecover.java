package com.dutra.dsCatalog.controller;

import com.dutra.dsCatalog.dtos.EmailDto;
import com.dutra.dsCatalog.dtos.NewPasswordDto;
import com.dutra.dsCatalog.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/auth")
public class AuthRecover {

    private final AuthService authService;
    public AuthRecover(AuthService emailService) {
        this.authService = emailService;
    }

    @PutMapping(value = "/recover-token")
    public ResponseEntity<Void> createRecoverToken(@Valid @RequestBody EmailDto body) {
        authService.createRecoverToken(body);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/new-password")
    public ResponseEntity<Void> saveNewPassword(@Valid @RequestBody NewPasswordDto newPasswordDto) {
        authService.saveNewPassword(newPasswordDto);
        return ResponseEntity.noContent().build();
    }
}
