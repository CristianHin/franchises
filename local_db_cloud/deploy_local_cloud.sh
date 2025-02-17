#!/bin/bash

cd ../franchises-ms
./gradlew build
docker build -t franchise-ms -f deployment/Dockerfile .

cd ../local_db_cloud/rds
terraform init
terraform plan

read -p "¿Deseas aplicar los cambios de Terraform? (s/n): " confirm
if [[ $confirm == [sS] || $confirm == [yY] ]]; then
    terraform apply -auto-approve

    MYSQL_HOST=$(terraform output -raw endpoint)
    MYSQL_USER=$(terraform output -raw username)
    MYSQL_PASSWORD=$(terraform output -raw password)

    echo "Iniciando contenedor Docker con las credenciales de la base de datos..."
    docker run -d \
        --name franchise-container \
        -p 8080:8080 \
        -e MYSQL_HOST=$MYSQL_HOST \
        -e MYSQL_USER=$MYSQL_USER \
        -e MYSQL_PASSWORD=$MYSQL_PASSWORD \
        franchise-ms

    echo "Contenedor iniciado con nombre 'franchise-container'. Puedes acceder a la aplicación en http://localhost:8080"

    read -p "Cuando hayas terminado de revisar, puedes escribir 's' para destruir los cambios de Terraform." confirmD
    if [[ $confirmD == [sS] || $confirmD == [yY] ]]; then
        echo "Deteniendo el contenedor 'franchise-container'..."
        docker stop franchise-container
        docker rm franchise-container
        terraform destroy -auto-approve
    else
        echo "Operación cancelada."
    fi
else
    echo "Operación cancelada."
fi