package ru.investportfolio.dto.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.investportfolio.database.entity.Role;
import ru.investportfolio.database.entity.User;
import ru.investportfolio.dto.UserCreateDTO;

import java.util.Collections;

@Component
@RequiredArgsConstructor
public class UserCreateMapper implements Mapper<UserCreateDTO, User> {

    private final PasswordEncoder passwordEncoder;

    @Override
    public User map(UserCreateDTO object) {
        User user = new User();
        copy(object, user);
        return user;
    }

    @Override
    public User map(UserCreateDTO fromObject, User toObject) {
        copy(fromObject, toObject);
        return toObject;
    }


    private void copy(UserCreateDTO object, User user) {
        user.setEmail(object.getEmail());
        user.setFirstname(object.getFirstname());
        user.setLastname(object.getLastname());
        user.setPassword(passwordEncoder.encode(object.getPassword()));
        user.setActive(true);
        user.setRoles(Collections.singleton(Role.USER));
    }
}
