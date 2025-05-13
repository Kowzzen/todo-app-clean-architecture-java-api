package org.isep.cleancode.presentation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;

import org.isep.cleancode.Todo; // La classe Todo est partagée
import org.isep.cleancode.application.TodoManager; // Dépendance vers la couche Application

import spark.Request;
import spark.Response;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

// Cette classe gère la réception des requêtes HTTP et l'envoi des réponses
public class TodoController {

    // Configuration de Gson (inchangée par rapport à l'étape 1)
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new JsonSerializer<LocalDate>() {
                @Override
                public JsonPrimitive serialize(LocalDate src, Type typeOfSrc, com.google.gson.JsonSerializationContext context) {
                    return new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE));
                }
            })
            .registerTypeAdapter(LocalDate.class, new JsonDeserializer<LocalDate>() {
                @Override
                public LocalDate deserialize(com.google.gson.JsonElement json, Type typeOfT, com.google.gson.JsonDeserializationContext context) throws JsonParseException {
                    try {
                        return LocalDate.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE);
                    } catch (DateTimeParseException e) {
                        throw new JsonParseException("Invalid date format. Expected YYYY-MM-DD.", e);
                    }
                }
            })
            .create();

    // Dépendance vers le Manager (couche Application)
    private final TodoManager todoManager;

    // Injection de la dépendance via le constructeur
    public TodoController(TodoManager todoManager) {
        this.todoManager = todoManager;
    }

    // Endpoint GET /todos (appel au Manager pour obtenir les données)
    public Object getAllTodos(Request req, Response res) {
        res.type("application/json");

        // Appel au Manager pour récupérer la liste
        List<Todo> todos = todoManager.getAllTodos();

        // Sérialisation en JSON pour la réponse
        return gson.toJson(todos);
    }

    // Endpoint POST /todos (appel au Manager pour créer une tâche)
    public Object createTodo(Request req, Response res) {
        res.type("application/json");

        Todo newTodo;
        try {
            // Désérialisation JSON (comme en Étape 1)
            newTodo = gson.fromJson(req.body(), Todo.class);

            // Appel au Manager pour effectuer la création (incluant la validation métier)
            todoManager.createTodo(newTodo);

            // Si l'appel au manager réussit
            res.status(201);

            return gson.toJson(newTodo);

        } catch (JsonParseException e) {
            // Gère les erreurs de format JSON ou de date invalide (capturées par Gson/adaptateur)
            res.status(400); // Bad Request
            return gson.toJson(new ErrorResponse("Date Invalide : " + e.getMessage()));
        } catch (IllegalArgumentException e) {
            // Gère les erreurs de validation métier levées par le TodoManager
            res.status(400); // Bad Request
            return gson.toJson(new ErrorResponse(e.getMessage()));
        }
    }

    // Petite classe interne pour uniformiser les réponses d'erreur
    private static class ErrorResponse {
        String error;
        ErrorResponse(String error) {
            this.error = error;
        }
    }
}