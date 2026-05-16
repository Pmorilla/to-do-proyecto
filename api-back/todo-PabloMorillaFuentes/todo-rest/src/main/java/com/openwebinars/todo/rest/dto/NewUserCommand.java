package com.openwebinars.todo.rest.dto;

/**
 * DTO para la creación de un nuevo usuario.
 * Justificación: Encapsula los datos necesarios para el registro, permitiendo
 * validar la entrada de forma independiente a la entidad de persistencia.
 */
public record NewUserCommand(String username, String email, String password, String fullname) {
}
