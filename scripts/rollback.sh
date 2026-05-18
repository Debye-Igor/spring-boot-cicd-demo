#!/bin/bash
# Script de rollback Blue-Green
# Restaura el trafico a la version estable anterior en segundos

set -e

NGINX_CONF="docker/nginx.conf"
NGINX_CONTAINER="bluegreen-nginx"

echo "============================================="
echo " ROLLBACK Blue-Green - Restaurando estabilidad"
echo "============================================="

# Detectar version actualmente activa
CURRENT_ACTIVE=$(grep -E "^\s*server taskapi-(blue|green)" $NGINX_CONF | grep -v "^\s*#" | sed -E 's/.*taskapi-(blue|green).*/\1/')

if [ "$CURRENT_ACTIVE" = "blue" ]; then
    ROLLBACK_TARGET="green"
    echo "[INFO] Version activa actual: BLUE -> rollback a GREEN"
elif [ "$CURRENT_ACTIVE" = "green" ]; then
    ROLLBACK_TARGET="blue"
    echo "[INFO] Version activa actual: GREEN -> rollback a BLUE"
else
    echo "ERROR: No se pudo detectar la version activa en nginx.conf"
    exit 1
fi

# Ejecutar el switch
echo "[1/2] Ejecutando rollback a $ROLLBACK_TARGET..."
./scripts/deploy-bluegreen.sh $ROLLBACK_TARGET

echo "[2/2] Rollback completado"
echo "============================================="

# Verificacion final
sleep 2
CURRENT_VERSION=$(curl -s http://localhost/api/version | grep -o '"version":"[^"]*"')
echo " Version actual servida: $CURRENT_VERSION"
echo "============================================="