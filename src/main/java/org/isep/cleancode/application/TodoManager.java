package org.isep.cleancode.application;

import org.isep.cleancode.Todo;

import java.util.List;

import java.lang.IllegalArgumentException;


// Le cœur de la logique métier
public class TodoManager {

    // La dépendance est maintenant sur l'interface
    private final ITodoRepository todoRepository;

    // Injection de la dépendance via le constructeur - attend une implémentation de l'interface
    public TodoManager(ITodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }


    public void createTodo(Todo todo) {
        // --- Validation des règles métier ---

        // 1. Le nom est obligatoire
        if (todo.getName() == null || todo.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Un nom est requis.");
        }
        todo.setName(todo.getName().trim());

        // 2. Le nom doit être plus court que 64 caractères
        if (todo.getName().length() >= 64) {
            throw new IllegalArgumentException("Le nom doit etre inferieur à 64 caracteres.");
        }

        // 3. Le nom doit être unique
        // Appel au Repository via l'interface pour vérifier l'existence
        // Le Manager ne sait PAS comment existsByName est implémenté
        boolean nameAlreadyExists = todoRepository.existsByName(todo.getName());
        if (nameAlreadyExists) {
            throw new IllegalArgumentException("Tache avec le meme nom existe deja.");
        }

        // 4. Date d'échéance déjà gérée par la désérialisation/adaptateur Gson.

        // On demande au Repository (via l'interface) d'ajouter la tâche
        todoRepository.addTodo(todo);
    }

    public List<Todo> getAllTodos() {
        // On demande au Repository (via l'interface) de nous donner toutes les tâches
        return todoRepository.getAllTodos();
    }
}