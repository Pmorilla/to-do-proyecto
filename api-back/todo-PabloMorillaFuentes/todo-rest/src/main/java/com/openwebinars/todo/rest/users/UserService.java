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
                .fullname(comando.fullname())
                .role(UserRole.USER)
                .build();
        return userRepository.save(user);
    }

    public User update(User user, UpdateUserCommand command) {
        user.setEmail(command.email());
        user.setFullname(command.fullname());
        return userRepository.save(user);
    }



}
