package com.openwebinars.todo.rest.controller;

import com.openwebinars.todo.rest.dto.EditTaskDto;
import com.openwebinars.todo.rest.dto.GetTaskDto;
import com.openwebinars.todo.rest.dto.DashboardDto;
import com.openwebinars.todo.rest.service.TaskService;
import com.openwebinars.todo.rest.model.User;
import com.openwebinars.todo.rest.model.TaskPriority;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
 * Controlador para la gestión de tareas.
 * 
 * @author Pablo Morilla
 */
@RestController
@RequestMapping("/task")
@RequiredArgsConstructor
@SecurityRequirement(name = "basicAuth")
public class TaskController {

    private final TaskService taskService;

    @Operation(
            summary = "Obtener todas las tareas del usuario (con filtros opcionales)",
            description = "Permite obtener todas las tareas de un usuario, con capacidad de filtrado"
    )
    @ApiResponse(description = "Listado de tareas del usuario",
            responseCode = "200",
            content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = GetTaskDto.class))
            )
    )
    @GetMapping
    public List<GetTaskDto> getAll(
            @AuthenticationPrincipal User autor,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Boolean completed,
            @RequestParam(required = false) TaskPriority priority,
            @RequestParam(required = false) String tag
    ) {
        return taskService.search(autor, categoryId, completed, priority, tag)
                .stream()
                .map(GetTaskDto::of)
                .toList();
    }

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

    @Operation(summary = "Obtener panel de control/estadísticas de tareas")
    @GetMapping("/dashboard")
    public DashboardDto getDashboard(@AuthenticationPrincipal User autor) {
        return taskService.getDashboard(autor);
    }



}
