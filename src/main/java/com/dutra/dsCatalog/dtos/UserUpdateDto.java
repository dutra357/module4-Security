package com.dutra.dsCatalog.dtos;

import com.dutra.dsCatalog.services.validator.UserUpdateValid;

import java.io.Serializable;

@UserUpdateValid
public class UserUpdateDto extends UserDto implements Serializable {

}
