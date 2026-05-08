package com.openwebinars.todo.rest.dto;

import java.util.Map;

public record DashboardDto(
        long totalTasks,
        long completedTasks,
        long pendingTasks,
        long overdueTasks,
        Map<String, Long> tasksByCategory,
        Map<String, Long> tasksByTag
) {
}
