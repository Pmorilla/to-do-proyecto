package com.openwebinars.todo.rest.service;

import com.openwebinars.todo.rest.dto.NewUserCommand;
import com.openwebinars.todo.rest.model.User;
import com.openwebinars.todo.rest.model.UserRole;
import com.openwebinars.todo.rest.dto.UpdateUserCommand;
import com.openwebinars.todo.rest.repos.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

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



    public List<User> findAll() {
        return userRepository.findAll();
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    public User changeRole(Long id, UserRole newRole) {
        return userRepository.findById(id)
                .map(u -> {
                    u.setRole(newRole);
                    return userRepository.save(u);
                })
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

}
