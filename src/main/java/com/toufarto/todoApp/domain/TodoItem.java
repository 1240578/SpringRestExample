package com.toufarto.todoApp.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
public class TodoItem {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    private Long id;

    @Getter
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "list_id")
    private TodoList list;

    protected TodoItem() {}

    public TodoItem(String description) {
        this.description = description;
    }

    void setList(TodoList list) {
        this.list = list;
    }
}
