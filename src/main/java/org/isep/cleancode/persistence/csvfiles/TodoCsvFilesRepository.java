package org.isep.cleancode.persistence.csvfiles;

import org.isep.cleancode.Todo;
import org.isep.cleancode.application.ITodoRepository; // Implémente le Port Sortant

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors; // Pour simplifier existsByName


// Adaptateur pour la persistance dans un fichier CSV
public class TodoCsvFilesRepository implements ITodoRepository {

    private final Path csvFilePath; // Chemin vers le fichier CSV
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE; // Format de date YYYY-MM-DD

    public TodoCsvFilesRepository() {
        // Définie le chemin du fichier CSV.
        String userHome = System.getProperty("user.home"); // Répertoire de l'utilisateur
        Path appDataDir = Paths.get(userHome, ".todoapp");
        this.csvFilePath = appDataDir.resolve("todos.csv");


        try {
            Files.createDirectories(appDataDir);
            if (!Files.exists(csvFilePath)) { // Assurer que le répertoire existe au moment de l'initialisation du Repository
                Files.createFile(csvFilePath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Erreur pour Initialiser le fichier csv", e);
        }
    }




    private List<Todo> readTodosFromFile() {
        List<Todo> todos = new ArrayList<>();
        // Si le fichier n'existe pas encore (premier lancement), retourner une liste vide
        if (!Files.exists(csvFilePath)) {
            return todos;
        }

        try {

            List<String> lines = Files.readAllLines(csvFilePath);
            for (String line : lines) {
                if (line == null || line.trim().isEmpty()) continue;

                // Divise la ligne en parties (name, dueDate) - on limite à 2 parts pour gérer les virgules potentielles dans le nom
                String[] parts = line.split(",", 2);
                if (parts.length == 0 || parts[0].trim().isEmpty()) {

                    System.err.println("Manque un nom à cette tache" + line);
                    continue;
                }

                String name = parts[0].trim(); // Nettoyer le nom
                LocalDate dueDate = null;

                // Si une date d'échéance est présente dans la ligne
                if (parts.length > 1 && parts[1] != null && !parts[1].trim().isEmpty()) {
                    try {
                        // Tente de parser la date
                        dueDate = LocalDate.parse(parts[1].trim(), DATE_FORMATTER);
                    } catch (DateTimeParseException e) {
                        System.err.println("date invalide dans le fichier csv " + line + " - " + e.getMessage());
                        continue;
                    }
                }


                todos.add(new Todo(name, dueDate));
            }
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la lecture du fichier csv", e);
        }
        return todos;
    }

    // Ajoute une seule tâche formatée à la fin du fichier CSV
    private void appendTodoToFile(Todo todo) {

        String dueDateStr = (todo.getDueDate() == null) ? "" : todo.getDueDate().format(DATE_FORMATTER);
        // Créer la ligne CSV (format simple nom,date)
        String csvLine = todo.getName() + "," + dueDateStr;

        try {
            Files.write(csvFilePath, (csvLine + System.lineSeparator()).getBytes(),
                    StandardOpenOption.APPEND, StandardOpenOption.CREATE);
        } catch (IOException e) {

            throw new RuntimeException("Erreur lors de l'ecriture dans le fichier csv", e);
        }
    }

    //  Implémentation des méthodes de l'interface ITodoRepository

    @Override
    public void addTodo(Todo todo) {
        appendTodoToFile(todo);
    }

    @Override
    public List<Todo> getAllTodos() {
        return readTodosFromFile();
    }

    @Override
    public boolean existsByName(String name) {
        return readTodosFromFile().stream()
                .anyMatch(todo -> todo.getName().equalsIgnoreCase(name));
    }
}
