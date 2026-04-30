package com.openwebinars.todo.rest.controller;

import com.openwebinars.todo.rest.model.Tag;
import com.openwebinars.todo.rest.repos.TagRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tag")
@RequiredArgsConstructor
@SecurityRequirement(name = "basicAuth")
public class TagController {

    private final TagRepository tagRepository;

    @Operation(summary = "Obtener todas las etiquetas")
    @GetMapping
    public List<Tag> findAll() {
        return tagRepository.findAll();
    }

    @Operation(summary = "Crear una etiqueta")
    @PostMapping
    public ResponseEntity<Tag> create(@RequestBody Tag tag) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tagRepository.save(tag));
    }

    @Operation(summary = "Eliminar una etiqueta")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        tagRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
