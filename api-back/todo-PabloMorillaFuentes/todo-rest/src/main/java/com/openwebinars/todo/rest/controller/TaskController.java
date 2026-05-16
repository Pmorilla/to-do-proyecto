package com.openwebinars.todo.rest.controller;

import com.openwebinars.todo.rest.dto.EditTaskDto;
import com.openwebinars.todo.rest.dto.GetTaskDto;
import com.openwebinars.todo.rest.dto.DashboardDto;
import com.openwebinars.todo.rest.service.TaskService;
import com.openwebinars.todo.rest.model.User;
import com.openwebinars.todo.rest.model.TaskPriority;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *       Controlador para la gestión de tareas.
 * 
 * @author Pablo Morilla
 */
@RestController
@RequestMapping("/task")
@RequiredArgsConstructor
@SecurityRequirement(name = "basicAuth")
public class TaskController {

    private final TaskService taskService;

    // Listar todas las tareas del usuario autenticado
    @Operation(summary = "Listar todas las tareas del usuario")
    @GetMapping
    public List<GetTaskDto> getAll(@AuthenticationPrincipal User autor) {
        return taskService.findByAuthor(autor)
                .stream()
                .map(GetTaskDto::of)
                .toList();
    }

    // Buscar tareas por múltiples campos
    @Operation(summary = "Buscar tareas por múltiples criterios")
    @GetMapping("/search")
    public List<GetTaskDto> search(
            @AuthenticationPrincipal User autor,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean completed,
            @RequestParam(required = false) TaskPriority priority,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") java.time.LocalDateTime deadlineBefore,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") java.time.LocalDateTime deadlineAfter
    ) {
        return taskService.search(autor, title, description, category, completed, priority, tag, deadlineBefore, deadlineAfter)
                .stream()
                .map(GetTaskDto::of)
                .toList();
    }

    // Buscar tareas por un tag concreto (específico del enunciado)
    @Operation(summary = "Buscar tareas con un tag concreto")
    @GetMapping("/by-tag")
    public List<GetTaskDto> getByTag(@AuthenticationPrincipal User autor, @RequestParam String tag) {
        return taskService.search(autor, null, null, null, null, null, tag, null, null)
                .stream()
                .map(GetTaskDto::of)
                .toList();
    }

    // Obtener una tarea por su ID
    @Operation(
            summary = "Obtener una tarea concreta",
            description = "Permite obtener la una tarea concreta si se le proporciona un id"
    )
    @ApiResponse(description = "Información detallada de una tarea",
            responseCode = "200",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = GetTaskDto.class),
                    examples = {
                            @ExampleObject("""
                                    {
                                             "id": 1,
                                             "title": "Comprar alimentos",
                                             "description": "Hacer una lista de compras para el supermercado.",
                                             "createdAt": "2025-01-13T16:12:11.295172",
                                             "deadline": "2025-01-20T16:12:11.295172",
                                             "author": {
                                                 "id": 1,
                                                 "username": "Pablo Morilla",
                                                 "email": "pablo.morilla@example.com"
                                             }
                                         }
                                """)
                    }
            )
    )
    @PostAuthorize("returnObject.author.username == authentication.principal.username")
    @GetMapping("/{id}")
    public GetTaskDto getById(@PathVariable Long id) {
        return GetTaskDto.of(taskService.findById(id));

    }

    // Crear una nueva tarea para el usuario autenticado
    @Operation(
            summary = "Crear una tarea",
            description = "Permite crear una tarea asociada al usuario autenticado"
    )
    @ApiResponse(description = "Tarea recién creada",
            responseCode = "201",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = GetTaskDto.class),
                    examples = {
                            @ExampleObject("""
                                    {
                                             "id": 1,
                                             "title": "Comprar alimentos",
                                             "description": "Hacer una lista de compras para el supermercado.",
                                             "createdAt": "2025-01-13T16:12:11.295172",
                                             "deadline": "2025-01-20T16:12:11.295172",
                                             "author": {
                                                 "id": 1,
                                                 "username": "Pablo Morilla",
                                                 "email": "pablo.morilla@example.com"
                                             }
                                         }
                                """)
                    }
            )
    )
    @PostMapping
    public ResponseEntity<GetTaskDto> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Tarea a crear", required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = EditTaskDto.class),
                            examples = @ExampleObject("""
                                    {
                                         "title": "Aprender Spring Boot",
                                         "description": "Hacer todos los cursos de Spring Boot en Pmorilla.com",
                                         "deadline": "2025-12-31T23:59:59"
                                     }
                                """)
                    )
            )
            @RequestBody EditTaskDto peticion,
            @AuthenticationPrincipal User autor) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                GetTaskDto.of(taskService.save(peticion, autor))
        );
    }


    // Editar una tarea existente
    @Operation(
            summary = "Editar una tarea",
            description = "Permite editar una tarea asociada al usuario autenticado si se proporciona su ID"
    )
    @ApiResponse(description = "Tarea editada",
            responseCode = "200",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = GetTaskDto.class),
                    examples = {
                            @ExampleObject("""
                                    {
                                             "id": 1,
                                             "title": "Comprar alimentos",
                                             "description": "Hacer una lista de compras para el supermercado.",
                                             "createdAt": "2025-01-13T16:12:11.295172",
                                             "deadline": "2025-01-20T16:12:11.295172",
                                             "author": {
                                                 "id": 1,
                                                 "username": "Pablo Morilla",
                                                 "email": "pablo.morilla@example.com"
                                             }
                                         }
                                """)
                    }
            )
    )
    @PreAuthorize("hasRole('ADMIN') or @ownerCheck.check(#id, principal)")
    @PutMapping("/{id}")
    public GetTaskDto edit(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos a editar en la tarea", required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = EditTaskDto.class),
                            examples = @ExampleObject("""
                                    {
                                         "title": "Aprender Spring Boot",
                                         "description": "Hacer todos los cursos de Spring Boot en Pmorilla.com",
                                         "deadline": "2025-12-31T23:59:59"
                                     }
                                """)
                    )
            )
            @RequestBody EditTaskDto peticion,
            @PathVariable Long id) {
        return GetTaskDto.of(taskService.edit(peticion, id));
    }

    // Eliminar una tarea
    @Operation(summary = "Eliminar una tarea")
    @ApiResponse(description = "Respuesta correcta de tarea eliminada",
            responseCode = "204",
            content = @Content(schema = @Schema(implementation = Void.class)))
    @PreAuthorize("hasRole('ADMIN') or @ownerCheck.check(#id, principal)")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Dashboard con estadísticas de tareas
    @Operation(summary = "Dashboard de estadísticas")
    @GetMapping({"/dashboard", "/stats"})
    public DashboardDto getDashboard(@AuthenticationPrincipal User autor) {
        return taskService.getDashboard(autor);
    }

    // Asignar tags a una tarea
    @Operation(summary = "Asignar un tag a una tarea")
    @PostMapping("/{id}/tags")
    @PreAuthorize("@ownerCheck.check(#id, principal)")
    public GetTaskDto addTagToTask(@PathVariable Long id, @RequestBody Long tagId) {
        return GetTaskDto.of(taskService.addTagToTask(id, tagId));
    }

    // Eliminar un tag de una tarea
    @Operation(summary = "Eliminar un tag de una tarea")
    @DeleteMapping("/{id}/tags/{tagId}")
    @PreAuthorize("@ownerCheck.check(#id, principal)")
    public GetTaskDto removeTagFromTask(@PathVariable Long id, @PathVariable Long tagId) {
        return GetTaskDto.of(taskService.removeTagFromTask(id, tagId));
    }



}
