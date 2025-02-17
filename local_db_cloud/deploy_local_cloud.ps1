cd ../franchises-ms
./gradlew build
docker build -t franchise-ms -f deployment/Dockerfile .

cd ../local_db_cloud/rds
terraform init
terraform plan

$confirm = Read-Host "¿Deseas aplicar los cambios de Terraform? (s/n)"
if ($confirm -eq 's' -or $confirm -eq 'S' -or $confirm -eq 'y' -or $confirm -eq 'yes') {
    terraform apply -auto-approve

    $MYSQL_HOST = terraform output -raw endpoint
    $MYSQL_USER = terraform output -raw username
    $MYSQL_PASSWORD = terraform output -raw password

    Write-Host "Iniciando contenedor Docker con las credenciales de la base de datos..."
    docker run -d `
        --name franchise-container `
        -p 8080:8080 `
        -e MYSQL_HOST=$MYSQL_HOST `
        -e MYSQL_USER=$MYSQL_USER `
        -e MYSQL_PASSWORD=$MYSQL_PASSWORD `
        franchise-ms

    Write-Host "Contenedor iniciado con nombre 'franchise-container'. Puedes acceder a la aplicación en http://localhost:8080"

    $confirmD = Read-Host "Cuando hayas terminado de revisar, puedes escribir 's' para destruir los cambios de Terraform."
    if ($confirmD -eq 's' -or $confirmD -eq 'S' -or $confirmD -eq 'y' -or $confirmD -eq 'yes') {
        Write-Host "Deteniendo el contenedor 'franchise-container'..."
        docker stop franchise-container
        docker rm franchise-container
        terraform destroy -auto-approve
    } else {
        Write-Host "Operación cancelada."
    }
} else {
    Write-Host "Operación cancelada."
}