# Etapa 1: Build - compila la aplicacion con Maven
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copiar archivos del proyecto
COPY pom.xml .
COPY src ./src

# Compilar y empaquetar (saltamos tests porque ya corrieron en el pipeline)
RUN mvn clean package -DskipTests

# Etapa 2: Runtime - imagen final mas liviana, solo con JRE
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Instalar wget para el healthcheck
RUN apk add --no-cache wget

# Copiar el JAR generado en la etapa de build
COPY --from=build /app/target/*.jar app.jar

# Variables de entorno por defecto (se sobrescriben en docker-compose)
ENV APP_VERSION=1.0.0-BLUE
ENV SERVER_PORT=8080

# Puerto que expone el contenedor
EXPOSE 8080

# Healthcheck: Docker reinicia el contenedor si el endpoint /health falla
HEALTHCHECK --interval=10s --timeout=3s --start-period=20s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Comando para arrancar la aplicacion
ENTRYPOINT ["java", "-jar", "app.jar"]