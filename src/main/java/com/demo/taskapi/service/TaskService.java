package com.demo.taskapi.service;

import com.demo.taskapi.model.Task;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Servicio que maneja la logica de negocio de las tareas.
 * Usa almacenamiento en memoria (HashMap) para simplificar.
 */
@Service
public class TaskService {

    private final Map<Long, Task> tasks = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public List<Task> findAll() {
        return new ArrayList<>(tasks.values());
    }

    public Optional<Task> findById(Long id) {
        return Optional.ofNullable(tasks.get(id));
    }

    public Task create(Task task) {
        Long newId = idGenerator.getAndIncrement();
        task.setId(newId);
        tasks.put(newId, task);
        return task;
    }

    public Optional<Task> update(Long id, Task updatedTask) {
        if (!tasks.containsKey(id)) {
            return Optional.empty();
        }
        updatedTask.setId(id);
        updatedTask.setCreatedAt(tasks.get(id).getCreatedAt());
        tasks.put(id, updatedTask);
        return Optional.of(updatedTask);
    }

    public boolean delete(Long id) {
        return tasks.remove(id) != null;
    }

    public int count() {
        return tasks.size();
    }
}