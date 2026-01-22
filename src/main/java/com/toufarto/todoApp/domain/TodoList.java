package com.toufarto.todoApp.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
public class TodoList {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    private Long id;

    @Getter
    @Setter
    private String name;

    @OneToMany(mappedBy = "list", cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter
    private List<TodoItem> items;

    protected TodoList() {}

    public TodoList(String name) {
        this.name = name;
        items = new ArrayList<>();
    }

    public void addItem(TodoItem item) {
        items.add(item);
        item.setList(this);
    }

    public void removeItem(TodoItem item) {
        items.remove(item);
        item.setList(null);
    }

}
