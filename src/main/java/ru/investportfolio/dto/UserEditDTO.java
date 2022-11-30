package ru.investportfolio.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class UserEditDTO {

    @Email(message = "Email is not correct!")
    @NotBlank(message = "Email can't be empty!")
    private String email;

    private String firstname;

    private String lastname;

}
