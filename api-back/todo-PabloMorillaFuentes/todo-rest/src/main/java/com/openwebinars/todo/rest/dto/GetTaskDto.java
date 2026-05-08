package com.openwebinars.todo.rest.dto;

import com.openwebinars.todo.rest.model.Task;
import com.openwebinars.todo.rest.model.TaskPriority;
import com.openwebinars.todo.rest.dto.NewUserResponse;

import java.time.LocalDateTime;
import java.util.List;

public record GetTaskDto(
        Long id,
        String title,
        String description,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deadline,
        boolean completed,
        TaskPriority priority,
        NewUserResponse author,
        String category,
        List<String> tags
) {

    public static GetTaskDto of(Task t) {
        return new GetTaskDto(
                t.getId(),
                t.getTitle(),
                t.getDescription(),
                t.getCreatedAt(),
                t.getUpdatedAt(),
                t.getDeadline(),
                t.isCompleted(),
                t.getPriority(),
                NewUserResponse.of(t.getAuthor()),
                t.getCategory() != null ? t.getCategory().getName() : null,
                t.getTags().stream().map(tag -> tag.getName()).toList()
        );
    }

}
