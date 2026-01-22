package com.toufarto.todoApp.service;

import com.toufarto.todoApp.domain.TodoItem;
import com.toufarto.todoApp.domain.TodoList;
import com.toufarto.todoApp.repository.TodoListRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;

@Service
public class TodoService {
    private final TodoListRepository repository;

    public TodoService(TodoListRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public List<TodoList> findAll() {
        return repository.findAll();
    }

    @Transactional
    public TodoList createList(String name) {
        TodoList list = new TodoList(name);
        return repository.saveAndFlush(list);
    }

    @Transactional
    public void deleteList(Long id) {
        repository.deleteById(id);
    }

    @Transactional
    public TodoList getListOrThrow(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Transactional
    public TodoItem addItem(Long listId, String description) {
        TodoList list = getListOrThrow(listId);
        TodoItem item = new TodoItem(description);
        list.addItem(item);
        repository.saveAndFlush(list);
        return item;
    }

    @Transactional
    public void removeItem(Long listId, Long itemId) {
        TodoList list = getListOrThrow(listId);
        list.getItems().removeIf(item -> Objects.equals(item.getId(), itemId));
    }
}
