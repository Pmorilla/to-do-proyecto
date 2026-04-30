package com.openwebinars.todo.rest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.openwebinars.todo.rest.model.TaskPriority;

import java.time.LocalDateTime;
import java.util.List;

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
