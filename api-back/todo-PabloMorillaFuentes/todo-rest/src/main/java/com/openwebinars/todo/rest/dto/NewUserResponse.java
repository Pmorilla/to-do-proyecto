package com.openwebinars.todo.rest.dto;

import com.openwebinars.todo.rest.model.User;

/**
 * DTO para la respuesta tras la creación de un usuario o consulta de perfil.
 * Justificación: Filtra la información sensible (como la contraseña) y devuelve
 * solo los campos necesarios para la aplicación cliente.
 */
public record NewUserResponse(Long id, String username, String email, String fullname, String role) {

    public static NewUserResponse of(User user) {
        return new NewUserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullname(),
                user.getRole().name()
        );
    }

}
