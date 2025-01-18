package com.dutra.dsCatalog.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class EmailDto {
    @NotBlank(message = "Campo obrigatório.")
    @Email(message = "Insira um email válido.")
    private String email;

    public EmailDto() {}
    public EmailDto(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
