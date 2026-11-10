# Spring PetClinic - Modernized Backend

A modernized backend for the Spring PetClinic application. The project was migrated to **Java 21 LTS**, refactored into a **Hexagonal Architecture (Ports and Adapters)**, and equipped with a full **REST API** documented with **OpenAPI 3.0 (Swagger UI)**.

---

## Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Project Structure](#project-structure)
- [Tech Stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Build and Run](#build-and-run)
- [API Documentation (Swagger UI)](#api-documentation-swagger-ui)
- [REST Endpoints](#rest-endpoints)
- [Error Handling](#error-handling)
- [GitFlow Workflow](#gitflow-workflow)

---

## Overview

The original Spring PetClinic application was refactored to separate business logic from technical infrastructure and framework concerns:

- **Hexagonal Architecture**: Business models and use cases are isolated from persistence and web layers.
- **Java 21**: Upgraded to the modern LTS release.
- **REST API & Swagger**: Replaces legacy server-side page forms with clean JSON endpoints, input validation, and interactive documentation.
- **Automated Tests**: Preserves all existing functionality while adding comprehensive unit and integration tests.

---

## Architecture

The project follows the **Ports and Adapters** pattern:

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

### Key Design Points

1. **Separation of Concerns**: Core domain models do not depend on JPA annotations or Web MVC annotations.
2. **Ports as Contracts**: Use case interfaces define what the domain offers; repository port interfaces define what the domain needs.
3. **Data Validation**: Request DTOs enforce field rules (`@NotNull`, `@NotBlank`, `@Pattern`, `@Future`) using standard Jakarta Validation.
4. **Backward Compatibility**: The original Thymeleaf views and web controllers remain functional alongside the new REST endpoints.

---

## Project Structure

All new modular code is placed under `backend/spring-petclinic/src/main/java/org/springframework/samples/petclinic/`:

```
petclinic/
├── common/                               # Shared configuration and cross-cutting concerns
│   ├── config/OpenApiConfig.java         # Swagger and OpenAPI metadata
│   └── exception/GlobalExceptionHandler  # ProblemDetail (RFC 7807) error handler
├── vet/                                  # Veterinarians module
│   ├── domain/                           # VetModel, SpecialtyModel
│   ├── port/
│   │   ├── in/GetVetsUseCase.java        # Inbound Port
│   │   └── out/VetRepositoryPort.java    # Outbound Port
│   ├── service/VetService.java           # Use Case implementation
│   └── adapter/
│       ├── in/web/                       # VetRestController and DTOs
│       └── out/persistence/              # VetPersistenceAdapter
├── customer/                             # Owners and Pets module
│   ├── domain/                           # OwnerModel, PetModel, PetTypeModel
│   ├── port/
│   │   ├── in/                           # OwnerUseCase, PetUseCase
│   │   └── out/                          # OwnerRepositoryPort, PetRepositoryPort
│   ├── service/                          # OwnerService, PetService
│   └── adapter/
│       ├── in/web/                       # OwnerRestController, PetRestController, DTOs
│       └── out/persistence/              # OwnerPersistenceAdapter, PetPersistenceAdapter
├── visit/                                # Visits module
│   ├── domain/                           # VisitModel
│   ├── port/
│   │   ├── in/VisitUseCase.java          # Inbound Port
│   │   └── out/VisitRepositoryPort.java  # Outbound Port
│   ├── service/VisitService.java         # Visit business rules (future dates check)
│   └── adapter/
│       ├── in/web/                       # VisitRestController and DTOs
│       └── out/persistence/              # VisitPersistenceAdapter
└── system/                               # Application startup and actuator endpoints
```

---

## Tech Stack

- **Java**: 21 (LTS)
- **Framework**: Spring Boot 4.1.0
- **Data Access**: Spring Data JPA / Hibernate
- **API Docs**: SpringDoc OpenAPI 2.8.5 (Swagger UI)
- **Validation**: Jakarta Bean Validation (Hibernate Validator)
- **Databases**: H2 (in-memory, default), MySQL 9.7, PostgreSQL 18.4
- **Testing**: JUnit 5, Mockito, AssertJ, MockMvc, Testcontainers
- **Build**: Apache Maven (via included Maven Wrapper)

---

## Prerequisites

- **JDK 21** installed and configured in your path (`JAVA_HOME`).
  ```bash
  java -version
  ```
- **Git** for version control.
- **Docker** *(optional)*: Only required if you want to run integration tests against containerized MySQL or PostgreSQL. By default, in-memory H2 is used.

---

## Build and Run

Run all commands from the `backend/spring-petclinic` directory:

```bash
cd backend/spring-petclinic
```

### 1. Code Formatting
Format Java source files following Spring code style:
```bash
./mvnw spring-javaformat:apply
```
*(On Windows PowerShell, use `.\mvnw.cmd spring-javaformat:apply`)*

### 2. Compile
Compile all source classes with Java 21:
```bash
./mvnw clean compile
```

### 3. Run Tests
Run the entire test suite (unit tests, web slice tests, and integration tests):
```bash
./mvnw test
```

### 4. Package JAR
Package the executable application JAR:
```bash
./mvnw clean package -DskipTests
```
The output file is generated at `target/spring-petclinic-4.0.0-SNAPSHOT.jar`.

### 5. Start the Application
Run locally using the Spring Boot Maven plugin:
```bash
./mvnw spring-boot:run
```
Or run the packaged JAR:
```bash
java -jar target/spring-petclinic-4.0.0-SNAPSHOT.jar
```
The application starts at `http://localhost:8080`.

---

## API Documentation (Swagger UI)

When the application is running, you can explore and test the endpoints directly in your browser:

- **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI Schema (JSON)**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

---

## REST Endpoints

### 1. Veterinarians

| Method | Endpoint | Description | Status Code |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/vets` | List all vets and their specialties | `200 OK` |

**Example request:**
```bash
curl -X GET http://localhost:8080/api/v1/vets -H "Accept: application/json"
```

---

### 2. Owners

| Method | Endpoint | Description | Status Code |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/owners` | Register a new owner | `201 Created` |
| `GET` | `/api/v1/owners` | Search owners by last name | `200 OK` |
| `GET` | `/api/v1/owners/{ownerId}` | Get owner details by id | `200 OK` / `404 Not Found` |

**Example request (Create Owner):**
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

---

### 3. Pets

| Method | Endpoint | Description | Status Code |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/owners/{ownerId}/pets` | Add a new pet to an owner | `201 Created` |
| `GET` | `/api/v1/owners/{ownerId}/pets/types` | List all available pet types | `200 OK` |

**Example request (Add Pet):**
```bash
curl -X POST http://localhost:8080/api/v1/owners/1/pets \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Leo",
    "birthDate": "2023-01-15",
    "typeId": 1
  }'
```

---

### 4. Visits

| Method | Endpoint | Description | Status Code |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/owners/{ownerId}/pets/{petId}/visits` | Schedule a visit for a pet | `201 Created` |
| `GET` | `/api/v1/owners/{ownerId}/pets/{petId}/visits` | Get all visits for a pet | `200 OK` |

**Example request (Schedule Visit):**
```bash
curl -X POST http://localhost:8080/api/v1/owners/1/pets/7/visits \
  -H "Content-Type: application/json" \
  -d '{
    "date": "2026-12-01",
    "description": "Rabies vaccination"
  }'
```

---

## Error Handling

REST API errors return standard **RFC 7807 ProblemDetail** responses (`application/problem+json`).

Example validation error (`400 Bad Request`):
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

Example not found error (`404 Not Found`):
```json
{
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Owner not found with id: 99",
  "timestamp": "2026-11-10T19:30:00Z"
}
```

---

## GitFlow Workflow

The repository follows standard **GitFlow** branching:

- `main`: Stable release branch.
- `develop`: Integration branch for developed features.
- `feature/*`: Dedicated branches for each module and task.

### Commit History
All commits are written in simple and clear English:
- `Initial commit: import spring petclinic base project`
- `upgrade project to java 21 and add openapi dependencies`
- `add vets domain model, ports and service`
- `add vets rest controller and openapi documentation`
- `add customers domain models, ports and services`
- `add owners and pets rest controllers with openapi documentation`
- `add visit domain, service and rest controller`
- `add global exception handler and openapi configuration`
- `verify all unit and integration tests pass`
- `add project documentation and api reference`
