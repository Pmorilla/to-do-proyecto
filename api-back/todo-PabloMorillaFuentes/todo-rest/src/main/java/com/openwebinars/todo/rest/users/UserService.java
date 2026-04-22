package com.openwebinars.todo.rest.users;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * @author Pablo Morilla
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User register(NewUserCommand comando) {
        User user = User.builder()
                .username(comando.username())
                .email(comando.email())
                .password(passwordEncoder.encode(comando.password()))
                .isAdmin(false)
                .build();
        return userRepository.save(user);
    }



}
