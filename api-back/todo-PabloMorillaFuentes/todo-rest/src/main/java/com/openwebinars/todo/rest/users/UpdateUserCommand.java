package com.openwebinars.todo.rest.users;

public record UpdateUserCommand(String email, String fullname) {
}
