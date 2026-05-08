package com.openwebinars.todo.rest.service;

import com.openwebinars.todo.rest.dto.EditTaskDto;
import com.openwebinars.todo.rest.error.TaskNotFoundException;
import com.openwebinars.todo.rest.model.Tag;
import com.openwebinars.todo.rest.model.Task;
import com.openwebinars.todo.rest.repos.CategoryRepository;
import com.openwebinars.todo.rest.repos.TagRepository;
import com.openwebinars.todo.rest.repos.TaskRepository;
import com.openwebinars.todo.rest.model.User;
import com.openwebinars.todo.rest.model.TaskPriority;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import com.openwebinars.todo.rest.dto.DashboardDto;

/**
 * Servicio para gestión de tareas
 * @author Pablo Morilla
 */
@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;

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

    public List<Task> search(User autor, Long categoryId, Boolean completed, TaskPriority priority, String tagName) {
        List<Task> tasks = taskRepository.findByAuthor(autor);

        return tasks.stream()
                .filter(t -> categoryId == null || (t.getCategory() != null && t.getCategory().getId().equals(categoryId)))
                .filter(t -> completed == null || t.isCompleted() == completed)
                .filter(t -> priority == null || t.getPriority() == priority)
                .filter(t -> tagName == null || t.getTags().stream().anyMatch(tag -> tag.getName().equalsIgnoreCase(tagName)))
                .collect(Collectors.toList());
    }

    public Task findById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(()-> new TaskNotFoundException(id));
    }

    public Task save(EditTaskDto peticion, User autor) {
        Task task = Task.builder()
                .title(peticion.title())
                .description(peticion.description())
                .deadline(peticion.deadline())
                .completed(peticion.completed())
                .priority(peticion.priority())
                .author(autor)
                .build();

        if (peticion.categoryId() != null) {
            categoryRepository.findById(peticion.categoryId()).ifPresent(task::setCategory);
        }

        if (peticion.tags() != null) {
            task.setTags(peticion.tags().stream()
                    .map(name -> tagRepository.findByName(name).orElseGet(() -> tagRepository.save(Tag.builder().name(name).build())))
                    .collect(Collectors.toList()));
        }

        return taskRepository.save(task);
    }

    public Task edit(EditTaskDto peticion, Long id) {
        return taskRepository.findById(id)
                .map(t -> {
                    t.setTitle(peticion.title());
                    t.setDescription(peticion.description());
                    t.setDeadline(peticion.deadline());
                    t.setCompleted(peticion.completed());
                    t.setPriority(peticion.priority());

                    if (peticion.categoryId() != null) {
                        t.setCategory(categoryRepository.findById(peticion.categoryId()).orElse(null));
                    } else {
                        t.setCategory(null);
                    }

                    if (peticion.tags() != null) {
                        t.getTags().clear();
                        t.getTags().addAll(peticion.tags().stream()
                                .map(name -> tagRepository.findByName(name).orElseGet(() -> tagRepository.save(Tag.builder().name(name).build())))
                                .toList());
                    } else {
                        t.getTags().clear();
                    }

                    return taskRepository.save(t);
                })
                .orElseThrow(()-> new TaskNotFoundException(id));
    }


    public void delete(Long id) {
        taskRepository.deleteById(id);
    }

    public DashboardDto getDashboard(User autor) {
        List<Task> tasks = taskRepository.findByAuthor(autor);
        
        long total = tasks.size();
        long completed = tasks.stream().filter(Task::isCompleted).count();
        long pending = total - completed;
        
        LocalDateTime now = LocalDateTime.now();
        long overdue = tasks.stream()
                .filter(t -> !t.isCompleted() && t.getDeadline() != null && t.getDeadline().isBefore(now))
                .count();
                
        Map<String, Long> byCategory = tasks.stream()
                .filter(t -> t.getCategory() != null)
                .collect(Collectors.groupingBy(t -> t.getCategory().getName(), Collectors.counting()));
                
        Map<String, Long> byTag = new HashMap<>();
        tasks.forEach(t -> {
            t.getTags().forEach(tag -> {
                byTag.put(tag.getName(), byTag.getOrDefault(tag.getName(), 0L) + 1);
            });
        });
        
        return new DashboardDto(total, completed, pending, overdue, byCategory, byTag);
    }


}
