package com.openwebinars.todo.rest.dto;

public record NewUserCommand(String username, String email, String password, String fullname) {
}
