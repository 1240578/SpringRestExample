package com.toufarto.todoApp.domain.dto;

import com.toufarto.todoApp.domain.TodoList;

import java.util.List;

public record TodoListDto(Long id, String name, List<TodoItemDto> items) {
    public static TodoListDto from(TodoList e) {
        List<TodoItemDto> items = e.getItems() == null ? List.of() : e.getItems().stream().map(TodoItemDto::from).toList();
        return new TodoListDto(e.getId(), e.getName(), items);
    }
}
