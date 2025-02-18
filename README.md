# Proyecto Franquicias

***En esta guía encontrarás varias maneras de desplegar este proyecto, como en un contenedor local con la base de datos en la nube, toda la solución en la nube o toda la solución en local usando docker. Como proveedor en la nube se usa AWS con Terraform***

**NOTA**: La instancia de MySQL desplegada en la nube está configurada con pocos recursos y tarda en desplegar.

El servicio creado en Spring cuando se inicia ya crea la base de datos si no existe e inserta datos iniciales de prueba. Este script se puede encontrar en `./franchise-ms/applications/app-service/src/main/resources/scripts/init.sql`. Si no se tiene Docker o no se puede seguir alguno de estos pasos, se puede crear la base de datos en el servidor donde se vaya a ejecutar y cambiar las credenciales de acceso en `./franchise-ms/applications/app-service/src/main/resources/application.yaml` y ejecutar también el script en esta base de datos. Luego, ingresar a la raíz del servicio y ejecutar `./gradlew bootRun`. Cabe mencionar que si se sigue alguno de los tres pasos de despliegue que siguen, todo esto se hace de manera automática.

Para revisar los endpoints expuestos, puede consultar la colección que está en el directorio `/docs`. En esta URL puede encontrar más información: https://documenter.getpostman.com/view/17513204/2sAYXEFdmp

## Versiones de tecnologías utilizadas

* Gradle (8.8)
* Java (17.0.7)
* Terraform (1.10.5)

## 1. Despliegue Local con Base de Datos en la Nube

Este directorio contiene scripts para desplegar un entorno local con una base de datos MySQL en AWS utilizando Terraform y Docker. La configuración incluye el despliegue automático de un microservicio de gestión de franquicias que se conecta a la base de datos aprovisionada.

### Requisitos Previos

* Docker
* Terraform
* Terminal para ejecutar .ps1 o sh
* AWS CLI configurado con las credenciales apropiadas

### Estructura del despliegue

```text
local_db_cloud/
├── rds/
|── deploy_local_cloud.ps1
│── deploy_local_cloud.sh
```

### ¿Qué hace este script?

El script de despliegue realiza las siguientes operaciones en secuencia:

1. **Compilación y Containerización de la Aplicación**
   * Navega al directorio del microservicio de franquicias
   * Compila la aplicación usando Gradle
   * Crea una imagen Docker llamada 'franchise-ms'
2. **Aprovisionamiento de Infraestructura**
   * Inicializa Terraform en el directorio RDS
   * Planifica los cambios de infraestructura
   * Tras la confirmación, aplica la configuración de Terraform para crear:
     * Instancia RDS MySQL
     * Componentes de red necesarios
     * Grupos de seguridad
3. **Despliegue de la Aplicación**
   * Obtiene las credenciales de la base de datos desde las salidas de Terraform:
     * Endpoint de la base de datos
     * Usuario
     * Contraseña
   * Despliega el contenedor del microservicio de franquicias con:
     * Mapeo de puerto al 8080
     * Variables de entorno para la conexión a la base de datos
   * Proporciona acceso a la aplicación en http://localhost:8080
4. **Limpieza**
   * Ofrece la opción de destruir los recursos cuando se termine
   * Detiene y elimina el contenedor Docker
   * Destruye todos los recursos creados por Terraform

### Uso

1. `git clone https://github.com/CristianHin/franchises.git`
   `cd local_db_cloud`

2. Si estás en Windows puedes ejecutar `./deploy_local_cloud.ps1` y en Linux o Mac con `sh deploy_local_cloud.sh`

3. Seguir las indicaciones:
   * Introducir 'y' o 's' para confirmar los cambios de Terraform
   * El script desplegará la infraestructura y la aplicación
   * Acceder a la aplicación en http://localhost:8080
   * Cuando termine, introducir 'y' o 's' para limpiar los recursos

### Notas Importantes

* Asegúrese de que sus credenciales de AWS estén correctamente configuradas
* El script creará recursos reales en AWS - asegúrese de destruirlos cuando termine
* Todas las credenciales de la base de datos se gestionan y aseguran automáticamente
* El contenedor de la aplicación está configurado para conectarse automáticamente a la base de datos provisionada

### Limpieza

Los recursos se pueden limpiar de dos formas:

1. Usar la limpieza incorporada en el script (recomendado):
   * Introducir 'y' o 's' cuando se solicite la limpieza
   * Esto eliminará automáticamente todos los recursos creados
2. Manualmente:
   `docker stop franchise-container`
   `docker rm franchise-container`
   `terraform destroy -auto-approve`

<br>

## 2. Despliegue del servicio completo en la nube

Este directorio contiene scripts para desplegar todo el servicio en la nube usando AWS y Terraform. Al final de la ejecución se tendrá el DNS de un ALB para invocar los servicios.

### Requisitos Previos

* Docker
* Terraform
* Terminal para ejecutar .ps1 o sh
* AWS CLI configurado con las credenciales apropiadas

### Estructura del despliegue

```text
solution_cloud/
├── terraform/
|── deploy.ps1
│── deploy.sh
```

### ¿Qué hace este script?

El script de despliegue realiza las siguientes operaciones en secuencia:

1. **Compilación y Containerización de la Aplicación**
   * Navega al directorio del microservicio de franquicias
   * Compila la aplicación usando Gradle
   * Crea una imagen Docker llamada 'franchise-ms'
2. **Aprovisionamiento de Infraestructura**
   * Inicializa Terraform en el directorio terraform
   * Planifica los cambios de infraestructura
   * Tras la confirmación, aplica la configuración de Terraform para crear:
     * Instancia RDS MySQL
     * Cluster, servicio, ALB, TG y demás componentes necesarios para iniciar el servicio franchise-ms
     * Sube la imagen compilada previamente al repositorio privado en ECR
     * Componentes de red necesarios
     * Grupos de seguridad
3. **Despliegue de la Aplicación**
   * Proporciona acceso a la aplicación con un mensaje similar a este: "Puedes acceder a las pruebas desde Postman por el host (nombre completo del host)"
4. **Limpieza**
   * Ofrece la opción de destruir los recursos cuando se termine
   * Destruye todos los recursos creados por Terraform

### Uso

1. `git clone https://github.com/CristianHin/franchises.git`
   `cd solution_cloud`

2. Si estás en Windows puedes ejecutar `./deploy.ps1` y en Linux o Mac con `sh deploy.sh`

3. Seguir las indicaciones:
   * Introducir 'y' o 's' para confirmar los cambios de Terraform
   * El script desplegará la infraestructura y la aplicación
   * Acceder a la aplicación
   * Cuando termine, introducir 'y' o 's' para limpiar los recursos

<br>

## 3. Despliegue del servicio completo en local usando docker-compose

Este directorio contiene scripts para desplegar todo el servicio en local orquestando contenedores con Docker Compose.

### Requisitos Previos

* Docker - Docker Compose
* Terminal para ejecutar .ps1 o sh

### Estructura del despliegue

```text
local_docker_compose/
├── docker-compose.yaml
|── start.ps1
│── start.sh
```

### ¿Qué hace este script?

El script de despliegue realiza las siguientes operaciones en secuencia:

1. **Compilación y Containerización de la Aplicación**
   * Navega al directorio del microservicio de franquicias
   * Compila la aplicación usando Gradle
   * Crea una imagen Docker llamada 'franchise-ms'
2. **Aprovisionamiento de Infraestructura**
   * Realiza el up del Docker Compose e inicia una base de datos en MySQL y luego el servicio franchise-ms
3. **Despliegue de la Aplicación**
   * Proporciona acceso a la aplicación por http://localhost:8080
   * IMPORTANTE: La base de datos se inicia en el puerto 3306, verifique que no tenga otro servicio usando este puerto
4. **Limpieza**
   * Ejecutar `docker-compose down -v`

### Uso

1. `git clone https://github.com/CristianHin/franchises.git`
   `cd local_docker_compose`

2. Si estás en Windows puedes ejecutar `./start.ps1` y en Linux o Mac con `sh start.sh`

3. Al finalizar todo, ejecuta `docker-compose down -v` en la misma raíz para eliminar los recursos