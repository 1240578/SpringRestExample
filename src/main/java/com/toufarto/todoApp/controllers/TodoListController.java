package com.toufarto.todoApp.controllers;

import com.toufarto.todoApp.service.TodoService;
import com.toufarto.todoApp.domain.TodoItem;
import com.toufarto.todoApp.domain.TodoList;
import com.toufarto.todoApp.domain.dto.CreateTodoItemRequest;
import com.toufarto.todoApp.domain.dto.CreateTodoListRequest;
import com.toufarto.todoApp.domain.dto.TodoItemDto;
import com.toufarto.todoApp.domain.dto.TodoListDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;


@RestController
@RequestMapping("/api/v1/lists")
public class TodoListController {
    private final TodoService service;

    public TodoListController(TodoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<TodoListDto>> getTodoLists() {
        List<TodoListDto> body = service.findAll()
                .stream()
                .map(TodoListDto::from).toList();
        return ResponseEntity.ok(body);
    }

    @PostMapping()
    public ResponseEntity<TodoListDto> addTodoList(@Valid @RequestBody CreateTodoListRequest req, UriComponentsBuilder uri) {
        TodoList created = service.createList(req.name());
        URI location = uri.path("/api/v1/lists/{id}").buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(location).body(TodoListDto.from(created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TodoListDto> getTodoList(@PathVariable Long id) {
        TodoList list = service.getListOrThrow(id);
        return ResponseEntity.ok(TodoListDto.from(list));
    }

    @GetMapping("/{id}/todos")
    public ResponseEntity<List<TodoItemDto>> getTodosFromListId(@PathVariable Long id) {
        TodoList list = service.getListOrThrow(id);
        return ResponseEntity.ok(list.getItems().stream().map(TodoItemDto::from).toList());
    }


    @PostMapping("/{listId}/todos")
    public ResponseEntity<TodoItemDto> addTodoItem(@PathVariable Long listId, @Valid @RequestBody CreateTodoItemRequest req, UriComponentsBuilder uri) {
        TodoItem item = service.addItem(listId, req.description());
        URI location = uri.path("/api/v1/lists/{listId}/todos/{todoId}").buildAndExpand(listId, item.getId()).toUri();
        return ResponseEntity.created(location).body(TodoItemDto.from(item));
    }

    @DeleteMapping("/{listId}/todos/{todoId}")
    public ResponseEntity<Void> removeTodoItem(@PathVariable Long listId, @PathVariable Long todoId) {
        service.removeItem(listId, todoId);
        return ResponseEntity.noContent().build();
    }

}
