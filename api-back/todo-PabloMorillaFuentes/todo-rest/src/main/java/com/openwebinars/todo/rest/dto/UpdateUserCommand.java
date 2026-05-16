package com.openwebinars.todo.rest.dto;

/**
 * DTO para la actualización del perfil de un usuario.
 * Justificación: Define específicamente qué campos son editables por el usuario,
 * garantizando la seguridad y consistencia de los datos.
 */
public record UpdateUserCommand(String username, String email, String fullname, String password) {
}
