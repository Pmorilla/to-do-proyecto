package com.openwebinars.todo.rest.users;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador para operaciones de usuario.
 * @author Pablo Morilla
 */
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "Registro de un nuevo usuario")
    @PostMapping("/auth/register")
    public ResponseEntity<NewUserResponse> createUser(@RequestBody NewUserCommand comando) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(NewUserResponse.of(userService.register(comando)));
    }

    @Operation(summary = "Obtener el perfil del usuario autenticado")
    @SecurityRequirement(name = "basicAuth")
    @GetMapping("/me")
    public NewUserResponse getMe(@AuthenticationPrincipal User user) {
        return NewUserResponse.of(user);
    }

    @Operation(summary = "Actualizar el perfil del usuario autenticado")
    @SecurityRequirement(name = "basicAuth")
    @PutMapping("/me")
    public NewUserResponse updateMe(@AuthenticationPrincipal User user, @RequestBody UpdateUserCommand command) {
        return NewUserResponse.of(userService.update(user, command));
    }

}
