package com.openwebinars.todo.rest.controller;

import com.openwebinars.todo.rest.model.Category;
import com.openwebinars.todo.rest.repos.CategoryRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
@SecurityRequirement(name = "basicAuth")
public class CategoryController {

    private final CategoryRepository categoryRepository;

    @Operation(summary = "Obtener todas las categorías")
    @GetMapping
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Operation(summary = "Crear una categoría (Solo ADMIN)")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Category> create(@RequestBody Category category) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryRepository.save(category));
    }

    @Operation(summary = "Editar una categoría (Solo ADMIN)")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public Category update(@PathVariable Long id, @RequestBody Category category) {
        return categoryRepository.findById(id)
                .map(old -> {
                    old.setName(category.getName());
                    return categoryRepository.save(old);
                })
                .orElseThrow();
    }

    @Operation(summary = "Eliminar una categoría (Solo ADMIN)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        categoryRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
