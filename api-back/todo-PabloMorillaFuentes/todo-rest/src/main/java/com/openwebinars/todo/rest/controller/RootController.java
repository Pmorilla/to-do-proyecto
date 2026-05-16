package com.openwebinars.todo.rest.controller;

import com.openwebinars.todo.rest.dto.DashboardDto;
import com.openwebinars.todo.rest.model.Category;
import com.openwebinars.todo.rest.model.User;
import com.openwebinars.todo.rest.repos.CategoryRepository;
import com.openwebinars.todo.rest.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "basicAuth")
public class RootController {

    private final TaskService taskService;
    private final CategoryRepository categoryRepository;

    @Operation(summary = "Dashboard de estadísticas (Ruta Raíz)")
    @GetMapping("/dashboard")
    public DashboardDto getDashboard(@AuthenticationPrincipal User autor) {
        return taskService.getDashboard(autor);
    }

    @Operation(summary = "Listar categorías (Ruta Raíz)")
    @GetMapping("/categories")
    public List<Category> getCategories() {
        return categoryRepository.findAll();
    }
}
