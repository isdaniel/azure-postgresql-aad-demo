package com.example.demo;

/**
 * Todo domain class representing a todo item in the database.
 * This class is used to demonstrate CRUD operations with Azure PostgreSQL.
 */
public class Todo {
    
    private Long id;
    private String description;
    private String details;
    private boolean done;

    /**
     * Default constructor
     */
    public Todo() {
    }

    /**
     * Constructor with all fields
     * 
     * @param id          The unique identifier for the todo item
     * @param description A brief description of the todo item
     * @param details     Detailed information about the todo item
     * @param done        Whether the todo item is completed
     */
    public Todo(Long id, String description, String details, boolean done) {
        this.id = id;
        this.description = description;
        this.details = details;
        this.done = done;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    @Override
    public String toString() {
        return "Todo{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", details='" + details + '\'' +
                ", done=" + done +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Todo todo = (Todo) o;

        return id != null ? id.equals(todo.id) : todo.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}