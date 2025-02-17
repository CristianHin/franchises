cd ../franchises-ms
./gradlew build
docker build -t franchise-ms -f deployment/Dockerfile .

cd ../solution_cloud/terraform
terraform init
terraform plan

$confirm = Read-Host "¿Deseas aplicar los cambios de Terraform? (s/n)"
if ($confirm -eq 's' -or $confirm -eq 'S' -or $confirmD -eq 'y' -or $confirmD -eq 'yes') {
    terraform apply -auto-approve
    Read-Host "Puedes acceder a las prubas desde postman por el host http://$(terraform output -raw alb_dns_name):80"
    $confirmD = Read-Host "Cuando hayas terminado de revisar, puedes escribir 's' para destruir los cambios de Terraform. Recuerda eliminar la imagen del cluster antes de aplicar el destroy: "
    if ($confirmD -eq 's' -or $confirmD -eq 'S' -or $confirmD -eq 'y' -or $confirmD -eq 'yes') {
        terraform destroy -auto-approve
    } else {
        Write-Host "Operación cancelada."
    }
} else {
    Write-Host "Operación cancelada."
}