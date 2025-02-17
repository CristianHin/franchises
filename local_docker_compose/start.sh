#!/bin/bash

cd ../franchises-ms
./gradlew build

cd ../local_docker_compose

docker-compose up --build -d

echo "Mostrando logs... Presiona Ctrl+C para salir"
docker-compose logs -f