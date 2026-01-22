package com.toufarto.todoApp.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTodoItemRequest(
        @NotBlank(message = "Description required")
        @Size(max = 255, message = "name must be at most 255 characters")
        String description
) {}
