package com.toufarto.todoApp.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request body for creating a new Todo List.
 * Example JSON: { "name": "Groceries" }
 */
public record CreateTodoListRequest(
        @NotBlank(message = "name is required")
        @Size(max = 255, message = "name must be at most 255 characters")
        String name
) {}