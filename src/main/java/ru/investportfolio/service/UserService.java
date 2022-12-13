package ru.investportfolio.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.investportfolio.database.entity.User;
import ru.investportfolio.database.repository.UserRepository;
import ru.investportfolio.dto.UserCreateDTO;
import ru.investportfolio.dto.UserEditDTO;
import ru.investportfolio.dto.mapper.UserCreateMapper;
import ru.investportfolio.dto.mapper.UserEditMapper;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService implements UserDetailsService {

    private final static String USER_NOT_FOUND_BY_EMAIL_MSG = "User with email %s not found";
    private final static String USER_NOT_FOUND_BY_ID_MSG = "User with email %d not found";
    private final UserRepository userRepository;
    private final UserCreateMapper userCreateMapper;
    private final UserEditMapper userEditMapper;

    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(String.format(USER_NOT_FOUND_BY_EMAIL_MSG, email)));
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new UsernameNotFoundException(String.format(USER_NOT_FOUND_BY_ID_MSG, id)));
    }

    @Transactional
    public boolean create(UserCreateDTO userCreateDTO) {
        Optional<User> optionalUser = userRepository.findByEmail(userCreateDTO.getEmail());

        if (optionalUser.isPresent()) {
            return false;
        }
        userRepository.save(userCreateMapper.map(userCreateDTO));

        return true;
    }

    @Transactional
    public User update(User user, UserEditDTO userEditDTO) {
        return userRepository.saveAndFlush(userEditMapper.map(userEditDTO, user));
    }

    @Transactional
    public void updatePassword(User user, String password) {
        userRepository.findByEmail(user.getEmail())
                .map(entity -> {
                    entity.setPassword(passwordEncoder.encode(password));
                    return entity;
                })
                .map(userRepository::save);
    }

    @Transactional
    public boolean delete(Long id) {
        return userRepository.findById(id).map(entity -> {
            userRepository.delete(entity);
            userRepository.flush();
            return true;
        }).orElse(false);
    }
}
