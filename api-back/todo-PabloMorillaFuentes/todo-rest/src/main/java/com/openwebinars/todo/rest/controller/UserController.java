package com.openwebinars.todo.rest.controller;

import com.openwebinars.todo.rest.dto.NewUserCommand;
import com.openwebinars.todo.rest.dto.NewUserResponse;
import com.openwebinars.todo.rest.dto.UpdateUserCommand;
import com.openwebinars.todo.rest.model.User;
import com.openwebinars.todo.rest.model.UserRole;
import com.openwebinars.todo.rest.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Controlador para operaciones de usuario.
 * @author Pablo Morilla
 */
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // Registro de un nuevo usuario
    @Operation(summary = "Registro de un nuevo usuario")
    @PostMapping("/auth/register")
    public ResponseEntity<NewUserResponse> createUser(@RequestBody NewUserCommand comando) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(NewUserResponse.of(userService.register(comando)));
    }

    // Obtener el perfil del usuario autenticado
    @Operation(summary = "Obtener el perfil del usuario autenticado")
    @SecurityRequirement(name = "basicAuth")
    @GetMapping("/me")
    public NewUserResponse getMe(@AuthenticationPrincipal User user) {
        return NewUserResponse.of(user);
    }

    // Actualizar el perfil del usuario autenticado
    @Operation(summary = "Actualizar el perfil del usuario autenticado")
    @SecurityRequirement(name = "basicAuth")
    @PutMapping({"/me", "/profile"})
    public NewUserResponse updateMe(@AuthenticationPrincipal User user, @RequestBody UpdateUserCommand command) {
        return NewUserResponse.of(userService.update(user, command));
    }

    // Obtener todos los usuarios (Solo ADMIN)
    @Operation(summary = "Obtener todos los usuarios (Solo ADMIN)")
    @SecurityRequirement(name = "basicAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public List<NewUserResponse> getAllUsers() {
        return userService.findAll().stream().map(NewUserResponse::of).toList();
    }

    // Promocionar usuario a gestor (Solo ADMIN)
    @Operation(summary = "Promocionar usuario a gestor (Solo ADMIN)")
    @SecurityRequirement(name = "basicAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/users/{id}/promote")
    public NewUserResponse promoteToManager(@PathVariable Long id) {
        return NewUserResponse.of(userService.changeRole(id, UserRole.MANAGER));
    }

    // Degradar gestor a usuario (Solo ADMIN)
    @Operation(summary = "Degradar gestor a usuario (Solo ADMIN)")
    @SecurityRequirement(name = "basicAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/users/{id}/demote")
    public NewUserResponse demoteToUser(@PathVariable Long id) {
        return NewUserResponse.of(userService.changeRole(id, UserRole.USER));
    }

    // Eliminar un usuario (Solo ADMIN)
    @Operation(summary = "Eliminar un usuario (Solo ADMIN)")
    @SecurityRequirement(name = "basicAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
