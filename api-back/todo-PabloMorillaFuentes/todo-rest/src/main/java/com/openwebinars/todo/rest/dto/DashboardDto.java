package com.openwebinars.todo.rest.dto;

import java.util.Map;

/**
 * DTO para la representación de las estadísticas del dashboard.
 * Justificación: Agrupa diferentes métricas y conteos en un solo objeto de respuesta
 * para optimizar las peticiones del frontend.
 */
public record DashboardDto(
        long totalTasks,
        long completedTasks,
        long pendingTasks,
        long overdueTasks,
        Map<String, Long> tasksByCategory,
        Map<String, Long> tasksByTag
) {
}
