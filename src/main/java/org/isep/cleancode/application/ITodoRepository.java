
package org.isep.cleancode.application;



import org.isep.cleancode.Todo;
import java.util.List;

// Définit que la couche Application a besoin d'une implémentation de persistance.(csv ou inmemory)
public interface ITodoRepository {

    void addTodo(Todo todo); // Méthode pour ajouter une tâche
    List<Todo> getAllTodos(); // Méthode pour lister toutes les tâches

    // Méthode pour vérifier l'unicité
    boolean existsByName(String name);
}
