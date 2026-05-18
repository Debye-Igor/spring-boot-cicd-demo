# Spring Boot CI/CD Demo

Aplicacion Task API (gestion de tareas tipo CRUD) que sirve como base para demostrar un flujo completo de DevOps: control de versiones con GitFlow, integracion continua con GitHub Actions y despliegue Blue-Green con Docker.

Este proyecto fue desarrollado como parte del examen final del curso de Automatizacion de Pruebas de la carrera de Ingenieria en Informatica.

## Tecnologias usadas

- **Backend:** Java 17 + Spring Boot 3.5
- **Build:** Maven 3.9
- **Pruebas:** JUnit 5, Selenium WebDriver, Spring Boot Test
- **Containerizacion:** Docker + Docker Compose
- **CI/CD:** GitHub Actions
- **Router:** Nginx

## Estructura del proyecto

```
spring-boot-cicd-demo/
├── .github/workflows/      Pipeline de GitHub Actions
├── docker/                 Configuracion de Docker Compose y Nginx
├── scripts/                Scripts de deploy y rollback
├── src/
│   ├── main/java/          Codigo de la aplicacion
│   ├── main/resources/     Configuracion y archivos estaticos
│   └── test/java/          Pruebas unitarias, integracion y aceptacion
├── Dockerfile              Imagen Docker multi-stage
└── pom.xml                 Configuracion de Maven
```

## Como ejecutar el proyecto

### 1. Ejecutar la aplicacion localmente (sin Docker)

Requiere Java 17 y Maven 3.9 instalados.

```bash
./mvnw spring-boot:run
```

La aplicacion queda disponible en `http://localhost:8080`.

### 2. Ejecutar las pruebas

**Solo unitarias:**

```bash
./mvnw test
```

**Unitarias + integracion:**

```bash
./mvnw verify "-Dtest=!*AcceptanceTest"
```

### 3. Levantar el ambiente Blue-Green con Docker

Requiere Docker Desktop corriendo.

```bash
# Construir la imagen
docker build -t taskapi:1.0.0 .

# Levantar el stack (blue + green + nginx)
cd docker
docker compose up -d
```

Endpoints disponibles:

- `http://localhost` - acceso a traves del router nginx
- `http://localhost:8081` - acceso directo a BLUE (v1.0.0)
- `http://localhost:8082` - acceso directo a GREEN (v2.0.0)

### 4. Cambiar entre versiones (deploy y rollback)

```bash
# Cambiar trafico a GREEN
./scripts/deploy-bluegreen.sh green

# Volver a BLUE (rollback)
./scripts/rollback.sh
```

## Estrategia de pruebas

El proyecto incluye 3 tipos de pruebas separadas para que el pipeline pueda ejecutarlas en distintas fases.

### Pruebas unitarias

- Ubicacion: `src/test/java/com/demo/taskapi/service/`
- Archivos terminan en `*Test.java`
- Las ejecuta Maven con el plugin **Surefire** en la fase `test`
- Validan la logica del `TaskService` sin levantar Spring
- Son rapidas (menos de 1 segundo)

### Pruebas de integracion

- Ubicacion: `src/test/java/com/demo/taskapi/controller/`
- Archivos terminan en `*IT.java`
- Las ejecuta Maven con el plugin **Failsafe** en la fase `verify`
- Levantan el contexto completo de Spring Boot y validan los endpoints REST
- Mas lentas (3-5 segundos) porque inicializan toda la app

### Pruebas de aceptacion

- Ubicacion: `src/test/java/com/demo/taskapi/acceptance/`
- Archivos terminan en `*AcceptanceTest.java`
- Usan **Selenium WebDriver** para probar la interfaz web
- Se ejecutan manualmente (no en cada push)

## Pipeline de Integracion Continua

El pipeline esta definido en `.github/workflows/ci.yml` y se ejecuta automaticamente en cada push a `main`, `develop` o cualquier rama `feature/*`.

Stages del pipeline:

1. **Checkout** - descarga el codigo
2. **Configurar JDK 17** - instala Java en el runner
3. **Compilar el proyecto** - `mvn clean compile`
4. **Pruebas unitarias** - `mvn test` (plugin Surefire)
5. **Pruebas de integracion** - `mvn verify` (plugin Failsafe)
6. **Empaquetar** - genera el JAR final
7. **Publicar artefactos** - sube reportes y el JAR como evidencia

## Estrategia de despliegue Blue-Green

La estrategia Blue-Green consiste en mantener dos versiones de la aplicacion corriendo en paralelo y cambiar el trafico de una a otra sin tiempo de caida.

- **BLUE** corre en el puerto 8081 (version "estable" actual)
- **GREEN** corre en el puerto 8082 (version "nueva" en pruebas)
- **Nginx** en el puerto 80 actua como router y decide a cual de las dos enviar el trafico

Cuando se quiere liberar una nueva version:

1. Se levanta GREEN con la nueva version
2. Se valida que GREEN responde bien (smoke test)
3. Se modifica `nginx.conf` para apuntar a GREEN
4. Se recarga nginx (sin detenerlo, sin downtime)
5. Si algo falla, se revierte el cambio (rollback a BLUE en segundos)

Los scripts `deploy-bluegreen.sh` y `rollback.sh` automatizan este proceso.

## Flujo de trabajo con Git

El proyecto usa **GitFlow** como estrategia de ramificacion:

- `main` - codigo en produccion
- `develop` - integracion de nuevas funcionalidades
- `feature/*` - cada nueva funcionalidad se desarrolla en su propia rama

Las features se integran a `develop` mediante Pull Requests, y solo despues de que el pipeline pase en verde.

## Autor

Debye Igor

## Licencia

MIT License - ver archivo LICENSE para detalles.
