package com.openwebinars.todo.rest.controller;

import com.openwebinars.todo.rest.model.Tag;
import com.openwebinars.todo.rest.model.User;
import com.openwebinars.todo.rest.repos.TagRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tag")
@RequiredArgsConstructor
@SecurityRequirement(name = "basicAuth")
public class TagController {

    private final TagRepository tagRepository;

    // Listar etiquetas del usuario autenticado
    @Operation(summary = "Obtener todas las etiquetas del usuario")
    @GetMapping
    public List<Tag> findAll(@AuthenticationPrincipal User user) {
        return tagRepository.findByUser(user);
    }

    // Crear una nueva etiqueta propia
    @Operation(summary = "Crear una etiqueta")
    @PostMapping
    public ResponseEntity<Tag> create(@RequestBody Tag tag, @AuthenticationPrincipal User user) {
        tag.setUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(tagRepository.save(tag));
    }

    // Editar una etiqueta propia
    @Operation(summary = "Editar una etiqueta")
    @PutMapping("/{id}")
    public Tag update(@PathVariable Long id, @RequestBody Tag tag, @AuthenticationPrincipal User user) {
        return tagRepository.findById(id)
                .filter(t -> t.getUser().getId().equals(user.getId()))
                .map(old -> {
                    old.setName(tag.getName());
                    return tagRepository.save(old);
                })
                .orElseThrow(() -> new RuntimeException("Tag no encontrada o no pertenece al usuario"));
    }

    // Eliminar una etiqueta propia
    @Operation(summary = "Eliminar una etiqueta")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, @AuthenticationPrincipal User user) {
        tagRepository.findById(id)
                .filter(t -> t.getUser().getId().equals(user.getId()))
                .ifPresent(tagRepository::delete);
        
        return ResponseEntity.noContent().build();
    }
}
