package com.openwebinars.todo.rest.security;

import com.openwebinars.todo.rest.model.Task;
import com.openwebinars.todo.rest.model.User;
import com.openwebinars.todo.rest.repos.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OwnerCheck {

    private final TaskRepository taskRepository;

    public boolean check(Task task, Long userId) {
        if (task != null && task.getAuthor() != null && task.getAuthor().getId() != null)
            return task.getAuthor().getId().equals(userId);
        return false;
    }

    public boolean check(Long taskId, User user) {
        if (user == null || user.getId() == null) return false;
        
        return taskRepository.findById(taskId)
                .map(t -> t.getAuthor() != null && t.getAuthor().getId().equals(user.getId()))
                .orElse(false);
    }

}
