#!/bin/bash

cd ../franchises-ms

echo "Ejecutando build de Gradle..."
./gradlew build

echo "Construyendo imagen Docker..."
docker build -t franchise-ms -f deployment/Dockerfile .

cd ../solution_cloud/terraform

echo "Inicializando Terraform..."
terraform init

echo "Planeando cambios de Terraform..."
terraform plan

read -p "¿Deseas aplicar los cambios de Terraform? (s/n): " confirm
if [[ $confirm == [sS] || $confirm == [sS][íÍ] ]]; then
    terraform apply -auto-approve
    echo "Puedes acceder a las prubas desde postman por el host http://$(terraform output -raw alb_dns_name):80"
    read -p "Cuando hayas terminado de revisar, puedes escribir 's' para destruir los cambios de Terraform. Recuerda eliminar la imagen del cluster antes de aplicar el destroy: " confirmD
    if [[ $confirmD == [sS] || $confirmD == [sS][íÍ] ]]; then
        terraform destroy -auto-approve
    else
        echo "Operación cancelada."
    fi
else
    echo "Operación cancelada."
fi