package com.dutra.dsCatalog.dtos;

import com.dutra.dsCatalog.entities.User;
import com.dutra.dsCatalog.services.validator.UserInsertValid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

@UserInsertValid
public class UserInsertDto extends UserDto implements Serializable {
    @NotBlank(message = "Campo obrigatório")
    @Size(min = 8, message = "Mínimo de 8 caracteres.")
    private String password;

    public UserInsertDto() {}
    public UserInsertDto(String password) {
        this.password = password;
    }

    public UserInsertDto(Long id, String firstName, String lastName, String email, String password) {
        super(id, firstName, lastName, email);
        this.password = password;
    }

    public UserInsertDto(User user, String password) {
        super(user);
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
