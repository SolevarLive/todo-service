package org.example.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Сущность задачи в системе
 */
@Entity
@Table (name = "tasks")
public class Task {

    /**
     * Уникальный идентификатор задачи
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Краткое название задачи
     */
    @Column(nullable = false)
    private String title;

    /**
     * Подробное описание задачи
     */
    private String description;

    /**
     * Флаг выполнения задачи
     */
    private boolean completed = false;

    /**
     * Флаг выполнения задачи
     */
    @Column(name = "due_date")
    private LocalDateTime dueDate;

    /**
     * Время создания задачи
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public Task() {}

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public boolean isCompleted() { return completed; }

    public void setCompleted(boolean completed) { this.completed = completed; }

    public LocalDateTime getDueDate() { return dueDate; }

    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

