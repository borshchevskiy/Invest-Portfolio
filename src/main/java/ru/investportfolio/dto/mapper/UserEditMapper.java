package ru.investportfolio.dto.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.investportfolio.database.entity.User;
import ru.investportfolio.dto.UserEditDTO;

@Component
@RequiredArgsConstructor
public class UserEditMapper implements Mapper<UserEditDTO, User> {

    @Override
    public User map(UserEditDTO object) {
        User user = new User();
        copy(object, user);
        return user;
    }

    @Override
    public User map(UserEditDTO fromObject, User toObject) {
        copy(fromObject, toObject);
        return toObject;
    }

    private void copy(UserEditDTO object, User user) {
        user.setEmail(object.getEmail());
        user.setFirstname(object.getFirstname());
        user.setLastname(object.getLastname());
    }
}
