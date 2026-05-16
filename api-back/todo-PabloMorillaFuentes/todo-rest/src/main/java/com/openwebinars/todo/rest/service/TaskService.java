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
import org.springframework.transaction.annotation.Transactional;

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
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final TagRepository tagRepository;
    private final CategoryRepository categoryRepository;

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

    public List<Task> search(User autor, String title, String description, String category, Boolean completed, TaskPriority priority, String tagName, LocalDateTime deadlineBefore, LocalDateTime deadlineAfter) {
        List<Task> tasks = taskRepository.findByAuthor(autor);

        return tasks.stream()
                .filter(t -> title == null || t.getTitle().toLowerCase().contains(title.toLowerCase()))
                .filter(t -> description == null || t.getDescription().toLowerCase().contains(description.toLowerCase()))
                .filter(t -> {
                    if (category == null || category.isEmpty()) return true;
                    if (t.getCategory() == null) return false;
                    // Buscamos por ID (si es número) o por nombre
                    return t.getCategory().getId().toString().equals(category) || 
                           t.getCategory().getName().equalsIgnoreCase(category);
                })
                .filter(t -> completed == null || t.isCompleted() == completed)
                .filter(t -> priority == null || t.getPriority() == priority)
                .filter(t -> tagName == null || t.getTags().stream().anyMatch(tag -> tag.getName().equalsIgnoreCase(tagName)))
                .filter(t -> deadlineBefore == null || (t.getDeadline() != null && t.getDeadline().isBefore(deadlineBefore)))
                .filter(t -> deadlineAfter == null || (t.getDeadline() != null && t.getDeadline().isAfter(deadlineAfter)))
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
                    .map(name -> tagRepository.findByNameAndUser(name, autor)
                            .orElseGet(() -> tagRepository.save(Tag.builder().name(name).user(autor).build())))
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
                                .map(name -> tagRepository.findByNameAndUser(name, t.getAuthor())
                                        .orElseGet(() -> tagRepository.save(Tag.builder().name(name).user(t.getAuthor()).build())))
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

    @Transactional
    public Task addTagToTask(Long taskId, Long tagId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new RuntimeException("Tag no encontrado"));
        
        task.getTags().add(tag);
        return taskRepository.save(task);
    }

    @Transactional
    public Task removeTagFromTask(Long taskId, Long tagId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));
        
        task.getTags().removeIf(t -> t.getId().equals(tagId));
        return taskRepository.save(task);
    }
}
