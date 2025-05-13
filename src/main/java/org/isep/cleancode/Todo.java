package org.isep.cleancode;

import java.time.LocalDate;

public class Todo {


    private String name;
    private LocalDate dueDate;
    public Todo() {
    }

    // Constructeurs
    public Todo(String name) {
        this.name = name;
    }
    public Todo(String name, LocalDate dueDate) {
        this.name = name;
        this.dueDate = dueDate;
    }
    // GET / SET

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    // Optionnel: toString pour faciliter le débogage
    @Override
    public String toString() {
        return "Todo{" +
                "name='" + name + '\'' +
                ", dueDate=" + dueDate +
                '}';
    }
}