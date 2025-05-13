package org.isep.cleancode.persistence.inmemory;

import org.isep.cleancode.Todo;
import org.isep.cleancode.application.ITodoRepository; // Importer l'interface

import java.util.ArrayList;
import java.util.List;


public class TodoInMemoryRepository implements ITodoRepository {
    private final List<Todo> todos = new ArrayList<>();

    @Override // Indique que la méthode implémente une méthode de l''interface ITodoRepository
    public void addTodo(Todo todo) {
        todos.add(todo);
    }

    @Override
    public List<Todo> getAllTodos() {

        return new ArrayList<>(todos);
    }

    @Override
    public boolean existsByName(String name) {
        return todos.stream().anyMatch(todo -> todo.getName().equalsIgnoreCase(name));
    }
}
