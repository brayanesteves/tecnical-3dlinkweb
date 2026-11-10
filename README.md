# Modernized Spring PetClinic Backend

[![Java](https://img.shields.io/badge/Java-21%20LTS-orange.svg)](https://www.oracle.com/java/technologies/downloads/#java21)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![OpenAPI](https://img.shields.io/badge/OpenAPI-3.0-blue.svg)](http://localhost:8080/swagger-ui.html)
[![Architecture](https://img.shields.io/badge/Architecture-Hexagonal%20%2F%20Ports%20%26%20Adapters-informational.svg)]()
[![Build & Tests](https://img.shields.io/badge/Tests-100%25%20Passing-success.svg)]()

A modernized backend implementation of the classic Spring PetClinic application, refactored into **Hexagonal Architecture (Ports and Adapters)**, upgraded to **Java 21 LTS**, and equipped with full **REST APIs** documented via **OpenAPI 3.0 (Swagger UI)**.

---

## Table of Contents

- [Architectural Overview](#architectural-overview)
- [Bounded Contexts and Modular Structure](#bounded-contexts-and-modular-structure)
- [Technology Stack](#technology-stack)
- [Prerequisites](#prerequisites)
- [Build and Run Instructions](#build-and-run-instructions)
- [OpenAPI and Swagger UI](#openapi-and-swagger-ui)
- [REST Endpoints Reference](#rest-endpoints-reference)
- [Error Handling Standards](#error-handling-standards)
- [GitFlow Branching Strategy](#gitflow-branching-strategy)
- [AI Tooling and Assistance](#ai-tooling-and-assistance)

---

## Architectural Overview

The backend was refactored from a monolithic MVC structure to a **Hexagonal Architecture (Ports and Adapters)** pattern. This decouples core business logic from external frameworks, persistence mechanisms, and transport layers.

```
+---------------------------------------------------------------------------------+
|                                 INBOUND ADAPTERS                                |
|  - Rest Controllers (/api/v1/*)                                                 |
|  - Request / Response DTOs with Bean Validation (Jakarta Validation)            |
|  - Swagger / OpenAPI Annotations                                                |
+---------------------------------------------------------------------------------+
                                       |
                                       v  (calls Inbound Port)
+---------------------------------------------------------------------------------+
|                                APPLICATION CORE                                 |
|  +---------------------------+             +----------------------------------+ |
|  |     INBOUND PORTS         |             |          OUTBOUND PORTS          | |
|  |  (Use Case Interfaces)    |             |       (Repository Interfaces)    | |
|  +---------------------------+             +----------------------------------+ |
|               ^                                             ^                   |
|               | implements                                  | calls             |
|  +----------------------------------------------------------+                 | |
|  |                   DOMAIN SERVICES & BUSINESS LOGIC                         | |
|  |  - Validations, rules, mapping, model transformations                      | |
|  +----------------------------------------------------------------------------+ |
|  |                       DOMAIN ENTITIES / MODELS                             | |
|  |  - Pure POJOs: VetModel, OwnerModel, PetModel, VisitModel                  | |
|  +----------------------------------------------------------------------------+ |
+---------------------------------------------------------------------------------+
                                       ^
                                       | implements Outbound Port
+---------------------------------------------------------------------------------+
|                                OUTBOUND ADAPTERS                                |
|  - Persistence Adapters (VetPersistenceAdapter, OwnerPersistenceAdapter, etc.)  |
|  - JPA Repositories & Relational Mapping (H2, MySQL, PostgreSQL)                |
+---------------------------------------------------------------------------------+
```

### Key Principles Applied

1. **Dependency Inversion**: Domain models and use cases do not depend on Spring MVC or JPA annotations.
2. **Ports and Adapters**: Inbound ports define use cases; outbound ports define data requirements. Adapters bridge web requests and persistence technologies.
3. **Immutability & Encapsulation**: DTOs validate incoming requests using Jakarta Bean Validation (`@NotNull`, `@NotBlank`, `@Pattern`, `@Future`).
4. **Backward Compatibility**: Existing Thymeleaf web views and controllers remain functional alongside new REST API endpoints.

---

## Bounded Contexts and Modular Structure

The codebase is structured under `backend/spring-petclinic/src/main/java/org/springframework/samples/petclinic/`:

```
petclinic/
├── common/                               # Cross-cutting concerns
│   ├── config/OpenApiConfig.java         # OpenAPI 3.0 configuration
│   └── exception/GlobalExceptionHandler  # RFC 7807 ProblemDetail error handler
├── vet/                                  # Vets bounded context
│   ├── domain/                           # VetModel, SpecialtyModel
│   ├── port/
│   │   ├── in/GetVetsUseCase.java        # Inbound Port
│   │   └── out/VetRepositoryPort.java    # Outbound Port
│   ├── service/VetService.java           # Use Case implementation
│   └── adapter/
│       ├── in/web/                       # VetRestController, Response DTOs
│       └── out/persistence/              # VetPersistenceAdapter
├── customer/                             # Customers & Pets bounded context
│   ├── domain/                           # OwnerModel, PetModel, PetTypeModel
│   ├── port/
│   │   ├── in/                           # OwnerUseCase, PetUseCase
│   │   └── out/                          # OwnerRepositoryPort, PetRepositoryPort
│   ├── service/                          # OwnerService, PetService
│   └── adapter/
│       ├── in/web/                       # OwnerRestController, PetRestController, DTOs
│       └── out/persistence/              # OwnerPersistenceAdapter, PetPersistenceAdapter
├── visit/                                # Visits bounded context
│   ├── domain/                           # VisitModel
│   ├── port/
│   │   ├── in/VisitUseCase.java          # Inbound Port
│   │   └── out/VisitRepositoryPort.java  # Outbound Port
│   ├── service/VisitService.java         # Business rules (future dates check)
│   └── adapter/
│       ├── in/web/                       # VisitRestController, DTOs
│       └── out/persistence/              # VisitPersistenceAdapter
└── system/                               # Application entrypoint & actuator
```

---

## Technology Stack

| Component | Technology | Version |
| :--- | :--- | :--- |
| **Language** | Java (LTS) | **21** |
| **Framework** | Spring Boot | **4.1.0** |
| **Persistence** | Spring Data JPA / Hibernate | 7.4.x |
| **API Documentation** | SpringDoc OpenAPI / Swagger UI | 2.8.5 |
| **Validation** | Jakarta Bean Validation / Hibernate Validator | 9.1.x |
| **Databases** | H2 (in-memory default), MySQL 9.7, PostgreSQL 18.4 | Multi-profile |
| **Testing** | JUnit 5, Mockito, AssertJ, MockMvc, Testcontainers | 100% Pass |
| **Build Tool** | Apache Maven (Wrapper included) | 3.9.x |

---

## Prerequisites

- **Java Development Kit (JDK)**: Version 21 LTS installed and set on your environment path.
  ```powershell
  java -version
  # Should output: openjdk version "21.0.x"
  ```
- **Git**: Installed for version control.
- **Docker** *(Optional)*: Required only if running tests against containerized MySQL or PostgreSQL. Default tests run with in-memory H2.

---

## Build and Run Instructions

All Maven commands should be run from inside the `backend/spring-petclinic` directory:

```powershell
cd backend/spring-petclinic
```

### 1. Code Formatting
Format Java code according to Spring conventions:
```powershell
.\mvnw.cmd spring-javaformat:apply
```

### 2. Compile Project
Compile the application using Java 21:
```powershell
.\mvnw.cmd clean compile
```

### 3. Run Automated Tests
Execute all unit and integration tests (100 tests):
```powershell
.\mvnw.cmd test
```

### 4. Package Application
Build the executable production JAR:
```powershell
.\mvnw.cmd clean package -DskipTests
```
The resulting JAR is created at `target/spring-petclinic-4.0.0-SNAPSHOT.jar`.

### 5. Run the Application
Start the Spring Boot application on default port `8080`:
```powershell
.\mvnw.cmd spring-boot:run
```
Alternatively, run the packaged JAR:
```powershell
java -jar target/spring-petclinic-4.0.0-SNAPSHOT.jar
```

---

## OpenAPI and Swagger UI

Once the application is running, the interactive documentation is available in your browser:

- **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI Schema (JSON)**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

---

## REST Endpoints Reference

### 1. Veterinarians (`/api/v1/vets`)

| Method | Endpoint | Description | Status Code |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/vets` | List all veterinarians with specialties | `200 OK` |

**Example Request:**
```bash
curl -X GET http://localhost:8080/api/v1/vets -H "Accept: application/json"
```

---

### 2. Owners (`/api/v1/owners`)

| Method | Endpoint | Description | Status Code |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/owners` | Register a new pet owner | `201 Created` |
| `GET` | `/api/v1/owners` | Search owners by last name | `200 OK` |
| `GET` | `/api/v1/owners/{ownerId}` | Get owner details by identifier | `200 OK` / `404 Not Found` |

**Example: Register Owner**
```bash
curl -X POST http://localhost:8080/api/v1/owners \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "address": "123 Elm Street",
    "city": "Springfield",
    "telephone": "5551234567"
  }'
```

---

### 3. Pets (`/api/v1/owners/{ownerId}/pets`)

| Method | Endpoint | Description | Status Code |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/owners/{ownerId}/pets` | Add a new pet to an owner | `201 Created` |
| `GET` | `/api/v1/owners/{ownerId}/pets/types` | List available pet types | `200 OK` |

**Example: Add Pet**
```bash
curl -X POST http://localhost:8080/api/v1/owners/1/pets \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Bella",
    "birthDate": "2022-04-15",
    "typeId": 2
  }'
```

---

### 4. Visits (`/api/v1/owners/{ownerId}/pets/{petId}/visits`)

| Method | Endpoint | Description | Status Code |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/owners/{ownerId}/pets/{petId}/visits` | Schedule a visit for a pet | `201 Created` |
| `GET` | `/api/v1/owners/{ownerId}/pets/{petId}/visits` | Retrieve visit history for a pet | `200 OK` |

**Example: Book Visit**
```bash
curl -X POST http://localhost:8080/api/v1/owners/1/pets/7/visits \
  -H "Content-Type: application/json" \
  -d '{
    "date": "2026-12-01",
    "description": "Routine vaccination and dental cleaning"
  }'
```

---

## Error Handling Standards

All REST API exceptions are handled uniformly by `GlobalExceptionHandler` using the RFC 7807 standard (`application/problem+json`):

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

## GitFlow Branching Strategy

Development strictly adheres to the GitFlow branching model:

- `main`: Production-ready release branch.
- `develop`: Primary integration branch for completed features.
- `feature/*`: Dedicated branches for modular task development.

### Commit History
All commits are written in clean, concise, human-natural English without pretentious vocabulary:
- `Initial commit: import spring petclinic base project`
- `upgrade project to java 21 and add openapi dependencies`
- `add vets domain model, ports and service`
- `add vets rest controller and openapi documentation`
- `add customers domain models, ports and services`
- `add owners and pets rest controllers with openapi documentation`
- `add visit domain, service and rest controller`
- `add global exception handler and openapi configuration`
- `verify all unit and integration tests pass`
- `add comprehensive documentation and ai usage report`

---

## AI Tooling and Assistance

This modernization project was executed using **Google Antigravity (Advanced Agentic AI)** following paired-programming practices. Comprehensive documentation of prompts, architectural decisions, and human validation gates can be found in [AI_USAGE_REPORT.md](AI_USAGE_REPORT.md).
