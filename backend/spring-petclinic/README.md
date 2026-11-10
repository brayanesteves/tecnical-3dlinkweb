# Spring PetClinic - Backend Service

[Español](#versión-en-español) | [English](#english-version)

---

# Versión en Español

## ¿De qué trata este servicio?

Este es el módulo backend de **Spring PetClinic**, refactorizado y modernizado. Implementa la lógica de negocio para la gestión integral de una veterinaria:

- Gestión de veterinarios y sus especialidades.
- Registro y búsqueda de dueños de mascotas.
- Registro de mascotas con su fecha de nacimiento y tipo de animal.
- Agendamiento de visitas médicas con validación de fechas futuras e historial de atenciones.

El backend fue migrado a **Java 21 LTS**, organizado bajo una **Arquitectura Hexagonal (Puertos y Adaptadores)** y expone una **API RESTful** documentada con **Swagger / OpenAPI 3.0**.

---

## Stack Tecnológico

- **Java**: 21 LTS
- **Spring Boot**: 4.1.0
- **Spring Data JPA**: Hibernate 7.4.x
- **Bases de datos soportadas**:
  - H2 Database (en memoria, perfil por defecto)
  - MySQL 9.7 (vía Docker Compose o configuración externa)
  - PostgreSQL 18.4 (vía Docker Compose o configuración externa)
- **Documentación OpenAPI**: SpringDoc OpenAPI 2.8.5 con Swagger UI
- **Validaciones**: Jakarta Bean Validation (Hibernate Validator)
- **Testing**: JUnit 5, Mockito, AssertJ, MockMvc, Testcontainers
- **Herramienta de compilación**: Apache Maven Wrapper (`mvnw` / `mvnw.cmd`)

---

## Cómo clonar y ubicarse en el backend

Si aún no has clonado el repositorio:

```bash
# Vía SSH
git clone git@github-own:brayanesteves/tecnical-3dlinkweb.git

# O vía HTTPS
git clone https://github.com/brayanesteves/tecnical-3dlinkweb.git
```

Luego navega directamente a este directorio:

```bash
cd tecnical-3dlinkweb/backend/spring-petclinic
```

---

## Cómo funciona la solución

El backend divide las responsabilidades en capas desacopladas mediante el patrón Hexagonal:

1. **Dominio (`domain`)**: Contiene modelos Java puros (`VetModel`, `OwnerModel`, `PetModel`, `VisitModel`), libres de dependencias de JPA o Spring.
2. **Puertos de Entrada (`port/in`)**: Interfaces que representan los casos de uso (`OwnerUseCase`, `PetUseCase`, `VisitUseCase`, `GetVetsUseCase`).
3. **Puertos de Salida (`port/out`)**: Interfaces que definen las operaciones de persistencia que el dominio necesita (`OwnerRepositoryPort`, etc.).
4. **Servicios (`service`)**: Implementan la lógica de negocio y las validaciones (por ejemplo, asegurar que las visitas se agenden en fechas futuras).
5. **Adaptadores Web (`adapter/in/web`)**: Controladores REST (`@RestController`) que reciben peticiones HTTP en `/api/v1/*`, validan los DTOs de entrada y devuelven respuestas JSON estandarizadas.
6. **Adaptadores de Persistencia (`adapter/out/persistence`)**: Conectan los puertos de salida con los repositorios de Spring Data JPA.
7. **Aspectos Transversales (`common`)**:
   - `GlobalExceptionHandler`: Captura errores de validación y de negocio, retornando el formato estándar RFC 7807 (`ProblemDetail`).
   - `OpenApiConfig`: Metadatos del esquema Swagger.

---

## Cómo levantarlo y ejecutar pruebas

Todos los comandos se ejecutan desde este directorio (`backend/spring-petclinic`):

### Requisito previo
Tener instalado JDK 21:
```bash
java -version
```

### 1. Formatear código
```bash
./mvnw spring-javaformat:apply
# En Windows: .\mvnw.cmd spring-javaformat:apply
```

### 2. Compilar
```bash
./mvnw clean compile
# En Windows: .\mvnw.cmd clean compile
```

### 3. Ejecutar pruebas automatizadas
```bash
./mvnw test
# En Windows: .\mvnw.cmd test
```

### 4. Levantar la aplicación
```bash
./mvnw spring-boot:run
# En Windows: .\mvnw.cmd spring-boot:run
```
La aplicación inicia en `http://localhost:8080`.

### 5. Empaquetar y ejecutar JAR
```bash
./mvnw clean package -DskipTests
# En Windows: .\mvnw.cmd clean package -DskipTests

java -jar target/spring-petclinic-4.0.0-SNAPSHOT.jar
```

---

## Documentación interactiva de la API

Con el backend en ejecución, puedes probar las peticiones directamente desde Swagger:

- **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **Esquema OpenAPI JSON**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

---

## Endpoints Principales

- `GET /api/v1/vets` - Lista de veterinarios y especialidades
- `POST /api/v1/owners` - Registro de nuevo dueño
- `GET /api/v1/owners` - Búsqueda de dueños
- `GET /api/v1/owners/{id}` - Detalle de dueño por ID
- `POST /api/v1/owners/{id}/pets` - Registro de mascota para un dueño
- `GET /api/v1/owners/{id}/pets/types` - Tipos de mascotas disponibles
- `POST /api/v1/owners/{ownerId}/pets/{petId}/visits` - Agendar cita para una mascota
- `GET /api/v1/owners/{ownerId}/pets/{petId}/visits` - Historial de visitas de una mascota

---
---

# English Version

## What is this service about?

This is the modernized backend service for **Spring PetClinic**. It manages the business operations of a veterinary practice:

- Managing veterinarians and their medical specialties.
- Registering and searching pet owners.
- Adding pets linked to an owner, including birth date and pet type.
- Booking veterinary visits with future-date checks and consultation history.

The backend is built on **Java 21 LTS**, structured under **Hexagonal Architecture (Ports and Adapters)**, and provides a **RESTful API** documented via **Swagger / OpenAPI 3.0**.

---

## Tech Stack

- **Java**: 21 LTS
- **Spring Boot**: 4.1.0
- **Spring Data JPA**: Hibernate 7.4.x
- **Databases**:
  - H2 Database (in-memory, default profile)
  - MySQL 9.7 (via Docker Compose or external config)
  - PostgreSQL 18.4 (via Docker Compose or external config)
- **OpenAPI Documentation**: SpringDoc OpenAPI 2.8.5 with Swagger UI
- **Validation**: Jakarta Bean Validation (Hibernate Validator)
- **Testing**: JUnit 5, Mockito, AssertJ, MockMvc, Testcontainers
- **Build Tool**: Apache Maven Wrapper (`mvnw` / `mvnw.cmd`)

---

## How to clone and navigate to the backend

If you have not cloned the repository yet:

```bash
# Using SSH
git clone git@github-own:brayanesteves/tecnical-3dlinkweb.git

# Or using HTTPS
git clone https://github.com/brayanesteves/tecnical-3dlinkweb.git
```

Then change into this directory:

```bash
cd tecnical-3dlinkweb/backend/spring-petclinic
```

---

## How the architecture works

The service decouples core logic from technical infrastructure using Hexagonal Architecture:

1. **Domain (`domain`)**: Pure Java models (`VetModel`, `OwnerModel`, `PetModel`, `VisitModel`), free from JPA or Spring framework dependencies.
2. **Inbound Ports (`port/in`)**: Interfaces representing business use cases (`OwnerUseCase`, `PetUseCase`, `VisitUseCase`, `GetVetsUseCase`).
3. **Outbound Ports (`port/out`)**: Interfaces defining persistence needs (`OwnerRepositoryPort`, `PetRepositoryPort`, `VisitRepositoryPort`, `VetRepositoryPort`).
4. **Services (`service`)**: Implement business rules and validations (e.g., verifying appointment dates are set in the future).
5. **Web Adapters (`adapter/in/web`)**: REST controllers (`@RestController`) that handle HTTP requests at `/api/v1/*`, validate request DTOs, and produce standardized JSON responses.
6. **Persistence Adapters (`adapter/out/persistence`)**: Implement outbound ports by interacting with Spring Data JPA repositories.
7. **Common (`common`)**:
   - `GlobalExceptionHandler`: Translates validation and business errors into RFC 7807 standard responses (`ProblemDetail`).
   - `OpenApiConfig`: Swagger documentation setup.

---

## How to build, test, and run

Run all commands from inside this directory (`backend/spring-petclinic`):

### Prerequisite
Ensure JDK 21 is installed:
```bash
java -version
```

### 1. Format code
```bash
./mvnw spring-javaformat:apply
# On Windows: .\mvnw.cmd spring-javaformat:apply
```

### 2. Compile
```bash
./mvnw clean compile
# On Windows: .\mvnw.cmd clean compile
```

### 3. Run automated tests
```bash
./mvnw test
# On Windows: .\mvnw.cmd test
```

### 4. Run the application
```bash
./mvnw spring-boot:run
# On Windows: .\mvnw.cmd spring-boot:run
```
The server starts at `http://localhost:8080`.

### 5. Package and run as a JAR
```bash
./mvnw clean package -DskipTests
# On Windows: .\mvnw.cmd clean package -DskipTests

java -jar target/spring-petclinic-4.0.0-SNAPSHOT.jar
```

---

## Interactive API Documentation

When the application is running, test endpoints via Swagger:

- **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI Schema (JSON)**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

---

## Main Endpoints

- `GET /api/v1/vets` - List vets and specialties
- `POST /api/v1/owners` - Register a new owner
- `GET /api/v1/owners` - Search owners by last name
- `GET /api/v1/owners/{id}` - Get owner by ID
- `POST /api/v1/owners/{id}/pets` - Add a pet to an owner
- `GET /api/v1/owners/{id}/pets/types` - List supported pet types
- `POST /api/v1/owners/{ownerId}/pets/{petId}/visits` - Schedule a visit for a pet
- `GET /api/v1/owners/{ownerId}/pets/{petId}/visits` - Retrieve pet visit history
