package com.demo.taskapi.acceptance;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PRUEBAS DE ACEPTACIÓN con Selenium.
 * Abren un navegador real y validan la UI de la app.
 * Por ahora se ejecutan manualmente; se integraran al pipeline en la Actividad 3.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("Task Web - Pruebas de Aceptaci\u00f3n con Selenium")
class TaskWebAcceptanceTest {

    @LocalServerPort
    private int port;

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeAll
    static void setUpClass() {
        // WebDriverManager descarga y configura chromedriver autom\u00e1ticamente
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");  // Sin abrir ventana (para CI)
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1280,800");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterEach
    void tearDown() {
        if (driver != null) driver.quit();
    }

    @Test
    @DisplayName("La p\u00e1gina principal debe cargar y mostrar el badge de versi\u00f3n")
    void paginaPrincipalCargaConVersion() {
        driver.get("http://localhost:" + port);

        WebElement badge = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.id("versionBadge"))
        );

        assertTrue(badge.getText().contains("BLUE") || badge.getText().contains("GREEN"),
            "El badge debe mostrar BLUE o GREEN, encontrado: " + badge.getText());
    }

    @Test
    @DisplayName("Debe poder agregar una tarea desde la UI")
    void debeAgregarTareaDesdeUI() {
        driver.get("http://localhost:" + port);

        driver.findElement(By.id("taskTitle")).sendKeys("Tarea Selenium");
        driver.findElement(By.id("taskDesc")).sendKeys("Creada con Selenium WebDriver");
        driver.findElement(By.tagName("button")).click();

        WebElement list = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.id("taskList"))
        );

        wait.until(driver -> list.getText().contains("Tarea Selenium"));
        assertTrue(list.getText().contains("Tarea Selenium"));
    }
}