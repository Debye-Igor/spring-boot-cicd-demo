#!/bin/bash
# Script de despliegue Blue-Green
# Automatiza el switch del trafico entre versiones validando salud previa

set -e  # Termina el script si cualquier comando falla

# Configuracion
NGINX_CONF="docker/nginx.conf"
NGINX_CONTAINER="bluegreen-nginx"
TARGET=${1:-green}  # Por defecto, target es "green"

echo "============================================="
echo " Despliegue Blue-Green - Target: $TARGET"
echo "============================================="

# Paso 1: Validar salud del entorno destino
if [ "$TARGET" = "green" ]; then
    HEALTH_URL="http://localhost:8082/actuator/health"
    UPSTREAM_OLD="server taskapi-blue:8080;"
    UPSTREAM_NEW="server taskapi-green:8080;"
elif [ "$TARGET" = "blue" ]; then
    HEALTH_URL="http://localhost:8081/actuator/health"
    UPSTREAM_OLD="server taskapi-green:8080;"
    UPSTREAM_NEW="server taskapi-blue:8080;"
else
    echo "ERROR: Target invalido. Usar 'blue' o 'green'."
    exit 1
fi

# Paso 2: Smoke test pre-switch
echo "[1/3] Validando salud de $TARGET en $HEALTH_URL..."
set +e  # Desactivar exit-on-error temporalmente para capturar el codigo HTTP
HEALTH_STATUS=$(curl -s -o /dev/null -w "%{http_code}" --max-time 5 $HEALTH_URL)
CURL_EXIT=$?
set -e  # Reactivar exit-on-error

if [ $CURL_EXIT -ne 0 ] || [ "$HEALTH_STATUS" != "200" ]; then
    echo "ERROR: $TARGET no esta saludable (HTTP $HEALTH_STATUS). Abortando despliegue."
    exit 1
fi
echo "      OK - $TARGET responde con HTTP 200"

# Paso 3: Actualizar nginx.conf
echo "[2/3] Actualizando configuracion de nginx..."
sed -i "s|^\s*server taskapi-.*:8080;|    $UPSTREAM_NEW|" $NGINX_CONF
echo "      OK - nginx.conf apunta a $TARGET"

# Paso 4: Recargar nginx sin downtime
echo "[3/3] Recargando nginx..."
docker exec $NGINX_CONTAINER nginx -s reload
echo "      OK - nginx recargado"

echo "============================================="
echo " Despliegue completado: trafico ahora en $TARGET"
echo "============================================="

# Verificacion final
sleep 2
CURRENT_VERSION=$(curl -s http://localhost/api/version | grep -o '"version":"[^"]*"')
echo " Version actual servida: $CURRENT_VERSION"
