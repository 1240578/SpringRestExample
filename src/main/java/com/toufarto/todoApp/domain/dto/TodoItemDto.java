package com.toufarto.todoApp.domain.dto;

import com.toufarto.todoApp.domain.TodoItem;

public record TodoItemDto(Long id, String description) {
    public static TodoItemDto from(TodoItem e) {
        return new TodoItemDto(e.getId(), e.getDescription());
    }
}
