package ru.investportfolio.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class UserEditDTO {

    @Email(message = "{user-edit-dto.email.email}")
    @NotBlank(message = "{user-edit-dto.email.blank}")
    private String email;

    private String firstname;

    private String lastname;

}
