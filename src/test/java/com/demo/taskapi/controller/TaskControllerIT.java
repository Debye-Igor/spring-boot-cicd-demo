package com.demo.taskapi.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * PRUEBAS DE INTEGRACIÓN del TaskController.
 * Levantan el contexto completo de Spring Boot y prueban los endpoints REST end-to-end.
 * Terminan en *IT.java para que las ejecute el plugin Failsafe (mvn verify).
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("TaskController - Pruebas de Integracion")
class TaskControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /api/version debe retornar la version configurada")
    void getVersionDebeRetornarVersion() throws Exception {
        mockMvc.perform(get("/api/version"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.version").exists());
    }

    @Test
    @DisplayName("GET /api/tasks debe retornar lista (puede estar vacía)")
    void getAllTasksDebeRetornarLista() throws Exception {
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("POST /api/tasks debe crear una tarea válida")
    void postTaskDebeCrearTareaValida() throws Exception {
        String taskJson = """
                {
                    "title": "Tarea de integración",
                    "description": "Creada desde test IT"
                }
                """;

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Tarea de integración"))
                .andExpect(jsonPath("$.completed").value(false));
    }

    @Test
    @DisplayName("POST /api/tasks con titulo vacio debe retornar 400")
    void postTaskSinTituloDebeRetornar400() throws Exception {
        String taskJson = """
                {
                    "title": "",
                    "description": "Sin título"
                }
                """;

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /actuator/health debe retornar UP")
    void healthCheckDebeRetornarUp() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }
}