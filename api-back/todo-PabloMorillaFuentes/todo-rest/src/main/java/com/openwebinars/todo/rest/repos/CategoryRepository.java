package com.openwebinars.todo.rest.repos;

import com.openwebinars.todo.rest.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
