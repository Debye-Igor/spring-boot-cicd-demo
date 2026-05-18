package com.demo.taskapi.service;

import com.demo.taskapi.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PRUEBAS UNITARIAS del TaskService.
 *  * No levantan Spring, son rápidas y prueban lógica de negocio aislada.
 * Se ejecutan con: mvn test (plugin Surefire)
 */
@DisplayName("TaskService - Pruebas Unitarias")
class TaskServiceTest {

    private TaskService taskService;

    @BeforeEach
    void setUp() {
        // Antes de cada test, creamos una instancia limpia
        taskService = new TaskService();
    }

    @Test
    @DisplayName("Debe crear una tarea y asignarle un ID")
    void debeCrearTareaYAsignarId() {
        Task task = new Task("Estudiar Maven", "Repasar el pom.xml");

        Task created = taskService.create(task);

        assertNotNull(created.getId(), "El ID no debe ser nulo");
        assertEquals("Estudiar Maven", created.getTitle());
        assertFalse(created.isCompleted(), "Una tarea nueva no debe estar completada");
    }

    @Test
    @DisplayName("Debe retornar todas las tareas creadas")
    void debeRetornarTodasLasTareas() {
        taskService.create(new Task("Tarea 1", "Desc 1"));
        taskService.create(new Task("Tarea 2", "Desc 2"));

        List<Task> tasks = taskService.findAll();

        assertEquals(2, tasks.size());
    }

    @Test
    @DisplayName("Debe encontrar una tarea por su ID")
    void debeEncontrarTareaPorId() {
        Task created = taskService.create(new Task("Buscar esta", "Test"));

        Optional<Task> found = taskService.findById(created.getId());

        assertTrue(found.isPresent());
        assertEquals("Buscar esta", found.get().getTitle());
    }

    @Test
    @DisplayName("Debe retornar Optional vac\u00edo si la tarea no existe")
    void debeRetornarVacioSiTareaNoExiste() {
        Optional<Task> found = taskService.findById(999L);

        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("Debe eliminar una tarea existente")
    void debeEliminarTareaExistente() {
        Task created = taskService.create(new Task("Eliminar", "Test"));

        boolean deleted = taskService.delete(created.getId());

        assertTrue(deleted);
        assertEquals(0, taskService.count());
    }

    @Test
    @DisplayName("Debe retornar false al intentar eliminar tarea inexistente")
    void debeRetornarFalseAlEliminarInexistente() {
        boolean deleted = taskService.delete(999L);

        assertFalse(deleted);
    }
}