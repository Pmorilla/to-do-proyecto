package com.openwebinars.todo.rest.service;

import com.openwebinars.todo.rest.dto.EditTaskDto;
import com.openwebinars.todo.rest.error.TaskNotFoundException;
import com.openwebinars.todo.rest.model.Task;
import com.openwebinars.todo.rest.repos.TaskRepository;
import com.openwebinars.todo.rest.users.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio para gestión de tareas
 * @author Pablo Morilla
 */
@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    public List<Task> findAll() {
        List<Task> result = taskRepository.findAll();

        if (result.isEmpty())
            throw new TaskNotFoundException();


        return result;
    }

    public List<Task> findByAuthor(User autor) {
        List<Task> result = taskRepository.findByAuthor(autor);

        if (result.isEmpty())
            throw new TaskNotFoundException();

        return result;
    }

    public Task findById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(()-> new TaskNotFoundException(id));
    }

    public Task save(EditTaskDto peticion, User autor) {
        return taskRepository.save(
                Task.builder()
                        .title(peticion.title())
                        .description(peticion.description())
                        .deadline(peticion.deadline())
                        .author(autor)
                        .build()
        );
    }

    public Task edit(EditTaskDto peticion, Long id) {
        return taskRepository.findById(id)
                .map(t -> {
                    t.setTitle(peticion.title());
                    t.setDescription(peticion.description());
                    t.setDeadline(peticion.deadline());
                    return taskRepository.save(t);
                })
                .orElseThrow(()-> new TaskNotFoundException(id));
    }


    public void delete(Long id) {
        taskRepository.deleteById(id);
    }


}
