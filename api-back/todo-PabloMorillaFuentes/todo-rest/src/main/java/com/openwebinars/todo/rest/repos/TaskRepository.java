package com.openwebinars.todo.rest.repos;

import com.openwebinars.todo.rest.model.Category;
import com.openwebinars.todo.rest.model.Tag;
import com.openwebinars.todo.rest.model.Task;
import com.openwebinars.todo.rest.model.TaskPriority;
import com.openwebinars.todo.rest.users.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByAuthor(User author);

    List<Task> findByAuthorAndCategory(User author, Category category);

    List<Task> findByAuthorAndCompleted(User author, boolean completed);

    List<Task> findByAuthorAndPriority(User author, TaskPriority priority);

    List<Task> findByAuthorAndTagsContains(User author, Tag tag);

}
