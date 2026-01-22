package com.toufarto.todoApp.repository;

import com.toufarto.todoApp.domain.TodoList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoListRepository extends JpaRepository<TodoList, Long> {}
