package com.demo.taskapi.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * Modelo que representa una tarea.
 * Incluye validaciones.
 */
public class Task {

    private Long id;

    @NotBlank(message = "El t\u00edtulo no puede estar vac\u00edo")
    @Size(min = 3, max = 100, message = "El t\u00edtulo debe tener entre 3 y 100 caracteres")
    private String title;

    @Size(max = 500, message = "La descripci\u00f3n no puede superar 500 caracteres")
    private String description;

    private boolean completed;
    private LocalDateTime createdAt;

    // Constructor vac\u00edo (requerido por Spring para deserializaci\u00f3n JSON)
    public Task() {
        this.createdAt = LocalDateTime.now();
        this.completed = false;
    }

    public Task(String title, String description) {
        this();
        this.title = title;
        this.description = description;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}