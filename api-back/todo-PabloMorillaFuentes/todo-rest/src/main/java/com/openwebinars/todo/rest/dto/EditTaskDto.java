package com.openwebinars.todo.rest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.openwebinars.todo.rest.model.TaskPriority;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para la creación y edición de tareas.
 * Justificación: Permite desacoplar la API de la estructura de la base de datos
 * y realizar validaciones de entrada específicas sin afectar a la entidad JPA.
 */
public record EditTaskDto(
        String title,
        String description,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime deadline,
        boolean completed,
        TaskPriority priority,
        Long categoryId,
        List<String> tags) {
}
