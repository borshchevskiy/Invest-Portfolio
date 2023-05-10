package ru.investportfolio.dto;

import lombok.Getter;
import lombok.Setter;
import ru.investportfolio.database.entity.Role;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Set;

@Getter
@Setter
public class UserCreateDTO {

    @Email(message = "{user-create-dto.email.email}")
    @NotBlank(message = "{user-create-dto.email.blank}")
    private String email;

    @NotBlank(message = "{user-create-dto.password.blank}")
    private String password;

    private String firstname;

    private String lastname;

    private Set<Role> roles;

    private boolean active;

}
