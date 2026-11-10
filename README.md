# Spring PetClinic - Modernized Backend

[Español](#versión-en-español) | [English](#english-version)

---

# Versión en Español

## ¿De qué trata el proyecto?

Este proyecto es una modernización del backend clásico de **Spring PetClinic**, un sistema para la gestión operativa de una clínica veterinaria. Permite administrar:

- **Veterinarios**: Consulta de profesionales y sus especialidades (radiología, cirugía, odontología, etc.).
- **Dueños (Owners)**: Registro, búsqueda por apellido y consulta de datos de contacto de los clientes.
- **Mascotas (Pets)**: Registro de animales asociados a un dueño, con su fecha de nacimiento y tipo (perro, gato, ave, etc.).
- **Visitas (Visits)**: Agendamiento de citas médicas y control del historial clínico de cada mascota.

El objetivo principal de esta modernización fue actualizar la aplicación a **Java 21 LTS**, desacoplar la lógica de negocio mediante una **Arquitectura Hexagonal (Puertos y Adaptadores)**, y construir una **API REST** documentada con **OpenAPI 3.0 (Swagger UI)**.

---

## Stack Tecnológico

- **Lenguaje**: Java 21 (LTS)
- **Framework**: Spring Boot 4.1.0
- **Persistencia**: Spring Data JPA / Hibernate
- **Bases de Datos**: H2 (en memoria para desarrollo y pruebas), MySQL 9.7 y PostgreSQL 18.4
- **Documentación de API**: SpringDoc OpenAPI 2.8.5 (Swagger UI)
- **Validación**: Jakarta Bean Validation (Hibernate Validator)
- **Pruebas**: JUnit 5, Mockito, AssertJ, MockMvc y Testcontainers
- **Gestor de Construcción**: Apache Maven (incluye Maven Wrapper `mvnw`)

---

## Cómo clonar el repositorio

Para clonar el proyecto completo en tu máquina:

```bash
# Vía SSH (si tienes tu clave configurada)
git clone git@github-own:brayanesteves/tecnical-3dlinkweb.git

# O vía HTTPS
git clone https://github.com/brayanesteves/tecnical-3dlinkweb.git
```

Una vez clonado, entra a la carpeta del proyecto backend:

```bash
cd tecnical-3dlinkweb/backend/spring-petclinic
```

---

## Cómo funciona la arquitectura

Se aplicó una **Arquitectura Hexagonal (Puertos y Adaptadores)** para separar claramente las reglas de negocio de los detalles de infraestructura (controladores web y base de datos).

```
+---------------------------------------------------------------------------------+
|                              ADAPTADORES DE ENTRADA                             |
|  - Controladores REST (/api/v1/*)                                               |
|  - DTOs de Entrada/Salida con Bean Validation                                   |
|  - Anotaciones Swagger / OpenAPI 3.0                                            |
+---------------------------------------------------------------------------------+
                                       |
                                       v  (Llama al Puerto de Entrada)
+---------------------------------------------------------------------------------+
|                                 NÚCLEO DOMINIO                                  |
|  +---------------------------+             +----------------------------------+ |
|  |    PUERTOS DE ENTRADA     |             |        PUERTOS DE SALIDA         | |
|  |     (Casos de Uso)        |             |      (Interfaces Repository)     | |
|  +---------------------------+             +----------------------------------+ |
|               ^                                             ^                   |
|               | implementa                                  | consume           |
|  +----------------------------------------------------------+                 | |
|  |                    SERVICIOS Y REGLAS DE NEGOCIO                           | |
|  |  - Validaciones de fechas futuras, mapeos y lógica de aplicación           | |
|  +----------------------------------------------------------------------------+ |
|  |                          MODELOS DE DOMINIO                                | |
|  |  - Clases POJO puras: VetModel, OwnerModel, PetModel, VisitModel           | |
|  +----------------------------------------------------------------------------+ |
+---------------------------------------------------------------------------------+
                                       ^
                                       | implementa Puerto de Salida
+---------------------------------------------------------------------------------+
|                              ADAPTADORES DE SALIDA                              |
|  - Adaptadores de persistencia (VetPersistenceAdapter, etc.)                    |
|  - Repositorios JPA y entidades de base de datos (H2, MySQL, PostgreSQL)       |
+---------------------------------------------------------------------------------+
```

### Estructura modular del código

El código backend se organiza en paquetes dentro de `src/main/java/org/springframework/samples/petclinic/`:

- **`common/`**: Aspectos transversales, configuración centralizada de OpenAPI y manejador global de excepciones con el estándar RFC 7807 (`ProblemDetail`).
- **`vet/`**: Módulo de veterinarios (`domain`, `port/in`, `port/out`, `service`, `adapter/in/web`, `adapter/out/persistence`).
- **`customer/`**: Módulo de dueños y mascotas, gestión de tipos de animales y datos de contacto.
- **`visit/`**: Módulo de citas médicas, validación de fechas futuras e historial de consultas.

---

## Cómo levantarlo y probarlo

Asegúrate de ejecutar estos comandos dentro de `backend/spring-petclinic`.

### 1. Requisitos previos
- Tener instalado **Java 21**. Verifica tu versión ejecutando:
  ```bash
  java -version
  ```
- Opcional: Docker Desktop si deseas ejecutar las pruebas contra contenedores reales de MySQL o PostgreSQL.

### 2. Formatear el código
Para aplicar el formato oficial de Spring:
```bash
# En Linux/macOS
./mvnw spring-javaformat:apply

# En Windows PowerShell
.\mvnw.cmd spring-javaformat:apply
```

### 3. Compilar el proyecto
```bash
./mvnw clean compile
# En Windows: .\mvnw.cmd clean compile
```

### 4. Ejecutar todas las pruebas automatizadas
Corre las 100 pruebas unitarias, de integración y controladores:
```bash
./mvnw test
# En Windows: .\mvnw.cmd test
```

### 5. Levantar la aplicación en desarrollo
Inicia el servidor Spring Boot en el puerto `8080`:
```bash
./mvnw spring-boot:run
# En Windows: .\mvnw.cmd spring-boot:run
```

### 6. Empaquetar y ejecutar como archivo JAR
Para generar el archivo ejecutable listo para producción:
```bash
./mvnw clean package -DskipTests
# En Windows: .\mvnw.cmd clean package -DskipTests
```
El archivo JAR se crea en `target/spring-petclinic-4.0.0-SNAPSHOT.jar`. Lo inicias con:
```bash
java -jar target/spring-petclinic-4.0.0-SNAPSHOT.jar
```

---

## Documentación de la API (Swagger UI)

Con la aplicación en marcha, puedes abrir en tu navegador:

- **Interfaz interactiva (Swagger UI)**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **Especificación OpenAPI (JSON)**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

---

## Endpoints de la API REST

### Veterinarios
- `GET /api/v1/vets`: Lista todos los veterinarios y sus especialidades.

```bash
curl -X GET http://localhost:8080/api/v1/vets -H "Accept: application/json"
```

### Dueños (Owners)
- `POST /api/v1/owners`: Crea un nuevo dueño con validaciones de campos.
- `GET /api/v1/owners`: Búsqueda de dueños por apellido (query param opcional `lastName`).
- `GET /api/v1/owners/{ownerId}`: Consulta el detalle de un dueño por su ID.

```bash
curl -X POST http://localhost:8080/api/v1/owners \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Carlos",
    "lastName": "Santana",
    "address": "Av. Principal 456",
    "city": "Madrid",
    "telephone": "6123456789"
  }'
```

### Mascotas (Pets)
- `POST /api/v1/owners/{ownerId}/pets`: Registra una nueva mascota vinculada a un dueño.
- `GET /api/v1/owners/{ownerId}/pets/types`: Lista los tipos de mascotas admitidos (gato, perro, lagarto, etc.).

```bash
curl -X POST http://localhost:8080/api/v1/owners/1/pets \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Max",
    "birthDate": "2023-05-10",
    "typeId": 2
  }'
```

### Visitas (Visits)
- `POST /api/v1/owners/{ownerId}/pets/{petId}/visits`: Agenda una cita médica (la fecha debe ser futura).
- `GET /api/v1/owners/{ownerId}/pets/{petId}/visits`: Obtiene el historial de consultas de una mascota.

```bash
curl -X POST http://localhost:8080/api/v1/owners/1/pets/7/visits \
  -H "Content-Type: application/json" \
  -d '{
    "date": "2026-12-15",
    "description": "Vacuna anual y revision general"
  }'
```

---

## Manejo de Errores

Todos los errores REST devuelven respuestas estandarizadas bajo **RFC 7807 (`application/problem+json`)**:

```json
{
  "type": "https://petclinic.example.com/errors/validation-error",
  "title": "Validation Error",
  "status": 400,
  "detail": "Request validation failed",
  "instance": "/api/v1/owners",
  "timestamp": "2026-11-10T19:30:00Z",
  "errors": {
    "telephone": "Telephone must be exactly 10 digits",
    "firstName": "First name must not be blank"
  }
}
```

---

## Flujo GitFlow

El repositorio sigue la estrategia de ramas de GitFlow:

- `main`: Código listo para producción.
- `develop`: Rama principal de integración.
- `feature/*`: Ramas de trabajo para cada funcionalidad (`feature/java21-upgrade`, `feature/vets-module`, `feature/customers-module`, etc.).

---
---

# English Version

## What is this project about?

This project is a modernization of the classic **Spring PetClinic** backend, a management system for a veterinary clinic. It handles:

- **Veterinarians**: Viewing clinic vets and their specialties (radiology, surgery, dentistry, etc.).
- **Owners**: Registering clients, searching by last name, and checking contact details.
- **Pets**: Adding pets associated with an owner, including birth date and pet type (dog, cat, bird, etc.).
- **Visits**: Scheduling appointments and tracking medical history for each pet.

The goal of this modernization was to upgrade the application to **Java 21 LTS**, decouple business logic using a **Hexagonal Architecture (Ports and Adapters)**, and provide a clean **REST API** documented with **OpenAPI 3.0 (Swagger UI)**.

---

## Tech Stack

- **Language**: Java 21 (LTS)
- **Framework**: Spring Boot 4.1.0
- **Data Access**: Spring Data JPA / Hibernate
- **Databases**: H2 (in-memory for local dev and tests), MySQL 9.7, and PostgreSQL 18.4
- **API Documentation**: SpringDoc OpenAPI 2.8.5 (Swagger UI)
- **Validation**: Jakarta Bean Validation (Hibernate Validator)
- **Testing**: JUnit 5, Mockito, AssertJ, MockMvc, and Testcontainers
- **Build Tool**: Apache Maven (includes Maven Wrapper `mvnw`)

---

## How to clone the repository

Clone the full project to your machine:

```bash
# Using SSH
git clone git@github-own:brayanesteves/tecnical-3dlinkweb.git

# Or using HTTPS
git clone https://github.com/brayanesteves/tecnical-3dlinkweb.git
```

Then navigate to the backend directory:

```bash
cd tecnical-3dlinkweb/backend/spring-petclinic
```

---

## How the architecture works

The project uses **Hexagonal Architecture (Ports and Adapters)** to keep business logic independent of external frameworks, web controllers, and databases.

```
+---------------------------------------------------------------------------------+
|                                 INBOUND ADAPTERS                                |
|  - REST Controllers (/api/v1/*)                                                 |
|  - Request and Response DTOs with Bean Validation                               |
|  - OpenAPI 3.0 / Swagger Annotations                                            |
+---------------------------------------------------------------------------------+
                                       |
                                       v  (calls Inbound Port)
+---------------------------------------------------------------------------------+
|                                APPLICATION CORE                                 |
|  +---------------------------+             +----------------------------------+ |
|  |     INBOUND PORTS         |             |          OUTBOUND PORTS          | |
|  |     (Use Cases)           |             |       (Repository Ports)         | |
|  +---------------------------+             +----------------------------------+ |
|               ^                                             ^                   |
|               | implements                                  | calls             |
|  +----------------------------------------------------------+                 | |
|  |                   DOMAIN SERVICES & BUSINESS LOGIC                         | |
|  |  - Validations, rules, and model mapping                                   | |
|  +----------------------------------------------------------------------------+ |
|  |                       DOMAIN MODELS                                        | |
|  |  - Pure Java objects: VetModel, OwnerModel, PetModel, VisitModel           | |
|  +----------------------------------------------------------------------------+ |
+---------------------------------------------------------------------------------+
                                       ^
                                       | implements Outbound Port
+---------------------------------------------------------------------------------+
|                                OUTBOUND ADAPTERS                                |
|  - Persistence Adapters (VetPersistenceAdapter, OwnerPersistenceAdapter, etc.)  |
|  - Spring Data JPA Repositories (H2, MySQL, PostgreSQL)                         |
+---------------------------------------------------------------------------------+
```

### Module breakdown

Code is organized under `src/main/java/org/springframework/samples/petclinic/`:

- **`common/`**: Cross-cutting config, OpenAPI setup, and global exception handling using RFC 7807 (`ProblemDetail`).
- **`vet/`**: Veterinarians module (`domain`, `port/in`, `port/out`, `service`, `adapter/in/web`, `adapter/out/persistence`).
- **`customer/`**: Owners and pets module, handling owner records, pet details, and pet types.
- **`visit/`**: Visits module, handling appointment booking with future date validation and visit history.

---

## How to build and run

Make sure to run these commands inside the `backend/spring-petclinic` directory.

### 1. Prerequisites
- **Java 21** installed. Verify your version:
  ```bash
  java -version
  ```
- Optional: Docker Desktop if you want to run integration tests against real MySQL or PostgreSQL containers.

### 2. Format code
Apply standard Spring code formatting:
```bash
# On Linux/macOS
./mvnw spring-javaformat:apply

# On Windows PowerShell
.\mvnw.cmd spring-javaformat:apply
```

### 3. Compile
```bash
./mvnw clean compile
# On Windows: .\mvnw.cmd clean compile
```

### 4. Run automated tests
Runs all 100 unit tests, web slice tests, and integration tests:
```bash
./mvnw test
# On Windows: .\mvnw.cmd test
```

### 5. Run the application locally
Starts the Spring Boot server on port `8080`:
```bash
./mvnw spring-boot:run
# On Windows: .\mvnw.cmd spring-boot:run
```

### 6. Package and run as a JAR
To build the production JAR file:
```bash
./mvnw clean package -DskipTests
# On Windows: .\mvnw.cmd clean package -DskipTests
```
The JAR is generated at `target/spring-petclinic-4.0.0-SNAPSHOT.jar`. Start it with:
```bash
java -jar target/spring-petclinic-4.0.0-SNAPSHOT.jar
```

---

## API Documentation (Swagger UI)

With the app running, open your browser:

- **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI Schema (JSON)**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

---

## REST Endpoints Reference

### Veterinarians
- `GET /api/v1/vets`: List all vets and their specialties.

```bash
curl -X GET http://localhost:8080/api/v1/vets -H "Accept: application/json"
```

### Owners
- `POST /api/v1/owners`: Create a new owner with field validation.
- `GET /api/v1/owners`: Search owners by last name (optional `lastName` query param).
- `GET /api/v1/owners/{ownerId}`: Retrieve owner details by ID.

```bash
curl -X POST http://localhost:8080/api/v1/owners \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "George",
    "lastName": "Franklin",
    "address": "110 W. Liberty St.",
    "city": "Madison",
    "telephone": "6085551023"
  }'
```

### Pets
- `POST /api/v1/owners/{ownerId}/pets`: Add a pet to an owner.
- `GET /api/v1/owners/{ownerId}/pets/types`: List available pet types.

```bash
curl -X POST http://localhost:8080/api/v1/owners/1/pets \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Leo",
    "birthDate": "2023-01-15",
    "typeId": 1
  }'
```

### Visits
- `POST /api/v1/owners/{ownerId}/pets/{petId}/visits`: Book a visit (date must be in the future).
- `GET /api/v1/owners/{ownerId}/pets/{petId}/visits`: Get all visits for a pet.

```bash
curl -X POST http://localhost:8080/api/v1/owners/1/pets/7/visits \
  -H "Content-Type: application/json" \
  -d '{
    "date": "2026-12-01",
    "description": "Routine checkup and rabies shot"
  }'
```

---

## Error Handling

REST endpoints return standard **RFC 7807 ProblemDetail** errors (`application/problem+json`):

```json
{
  "type": "https://petclinic.example.com/errors/validation-error",
  "title": "Validation Error",
  "status": 400,
  "detail": "Request validation failed",
  "instance": "/api/v1/owners",
  "timestamp": "2026-11-10T19:30:00Z",
  "errors": {
    "telephone": "Telephone must be exactly 10 digits",
    "firstName": "First name must not be blank"
  }
}
```

---

## GitFlow Workflow

The repository follows standard **GitFlow** branching:

- `main`: Production-ready release branch.
- `develop`: Integration branch for developed features.
- `feature/*`: Dedicated branches for each module and task.
