// src/main/java/org/isep/cleancode/Main.java
package org.isep.cleancode;

import static spark.Spark.*;

// Importations des nouvelles classes dans les paquets appropriés
import org.isep.cleancode.presentation.TodoController;
import org.isep.cleancode.application.TodoManager;
import org.isep.cleancode.persistence.csvfiles.TodoCsvFilesRepository;
import org.isep.cleancode.application.ITodoRepository;

public class Main {

    public static void main(String[] args) {
        port(4567); // Configure le port SparkJava
/*:
        // --- Assemblage des couches --- ETAPE 2

        // 1. Créer le Repository (couche Persistance)
        TodoRepository todoRepository = new TodoRepository();

        // 2. Créer le Manager (couche Application), en lui injectant le Repository
        TodoManager todoManager = new TodoManager(todoRepository);

        // 3. Créer le Controller (couche Présentation), en lui injectant le Manager
        TodoController todoController = new TodoController(todoManager);

        // --- Assemblage des couches (Composition Root) ---
        // C'est ici qu'on décide quelle implémentation de Repository utiliser.

        // 1. Créer l'adaptateur de persistance spécifique (ici, en mémoire)
        // Cette instance concrète IMPLÉMENTE l'interface ITodoRepository (ETAPE 3)
        ITodoRepository todoRepositoryImplementation = new TodoRepository(); // On crée l'instance concrète

        // 2. Créer le Manager (noyau de l'application)
        // On lui INJECTE l'implémentation du Repository.
        // Le Manager dépend de l'interface ITodoRepository, pas de l'instance concrète.
        TodoManager todoManager = new TodoManager(todoRepositoryImplementation);

        // 3. Créer le Controller (adaptateur d'entrée)
        // On lui injecte le Manager. Le Controller dépend du Manager.
        TodoController todoController = new TodoController(todoManager); */
        ITodoRepository todoRepositoryImplementation = new TodoCsvFilesRepository(); // *** LE CHANGEMENT CLÉ EST ICI ***

        // Créer le Manager en lui injectant l'adaptateur CSV
        // Le Manager s'attend à ITodoRepository, donc n'importe quelle implémentation marche !
        TodoManager todoManager = new TodoManager(todoRepositoryImplementation);

        // Créer le Controller en lui injectant le Manager
        TodoController todoController = new TodoController(todoManager);

        // --- Définition des routes SparkJava ---
        // Les requêtes HTTP entrantes sont dirigées vers le Controller

        get("/todos", todoController::getAllTodos);
        post("/todos", todoController::createTodo);



        // Spark démarre automatiquement quand les routes sont définies
        System.out.println("Spark server started on port 4567. Access http://localhost:4567/todos");
        // Optionnel: attendre pour que le serveur ne s'arrête pas immédiatement si tu le lances depuis un terminal
        // Spark::awaitInitialization();
    }
}