# Technical Design: Modernization of Spring PetClinic to Java 21 and Modular Hexagonal Architecture

**Author:** Brayan Esteves  
**Date:** 2026-11-10  
**Status:** In Review  
**Target Project:** Spring PetClinic Backend Modernization (`tecnical-3dlinkweb`)

---

## 1. Overview & Business Context

The objective is to modernize the official Spring PetClinic monolithic application into a clean, maintainable, modular backend built on **Java 21 LTS** and **Spring Boot 3/4**, adhering to **Hexagonal Architecture (Ports & Adapters)** and **SOLID** principles.

The modernized system replaces the legacy Thymeleaf server-side rendering layer with a complete **RESTful API** documented via **OpenAPI 3.0 (Swagger UI)**, preserves all original business rules and database schemas (H2 in-memory, MySQL, PostgreSQL), incorporates comprehensive automated tests, establishes a **GitFlow** branching strategy with natural human English commit messages, and documents AI usage during the modernization process.

---

## 2. Architecture & Bounded Contexts

The modernization restructures the codebase into decoupled bounded contexts following Hexagonal Architecture (Ports and Adapters). Each context is internally organized into three distinct layers:

```
[Inbound Adapters (REST Controllers / OpenAPI)]
                      ↓
[Inbound Ports (Use Cases / Application Services)]
                      ↓
              [Domain Model]
                      ↓
[Outbound Ports (Repository & Notification Interfaces)]
                      ↓
[Outbound Adapters (Spring Data JPA / DB Adapters)]
```

### Bounded Contexts:

1. **`customers` (Owners & Pets):**
   - **Domain:**
     - `Owner`: Aggregate root maintaining contact info and list of pets.
     - `Pet`: Entity with name, birth date, pet type, and owner association.
     - `PetType`: Catalog entity representing the species/type of animal.
   - **Application (Ports):**
     - Inbound: `OwnerUseCase` (find owners by last name, get owner details, create owner, update owner), `PetUseCase` (add pet to owner, update pet, list pet types).
     - Outbound: `OwnerRepositoryPort`, `PetRepositoryPort`, `PetTypeRepositoryPort`.
   - **Infrastructure (Adapters):**
     - Inbound Web: `OwnerRestController`, `PetRestController`, request/response DTOs with Bean Validation.
     - Outbound Persistence: `JpaOwnerRepository`, `JpaPetRepository`, `JpaPetTypeRepository`, mapping between JPA entities and domain models.

2. **`vets` (Veterinarians & Specialties):**
   - **Domain:**
     - `Vet`: Veterinarian professional details and specialized medical areas.
     - `Specialty`: Medical certification/specialty (radiology, surgery, dentistry).
   - **Application (Ports):**
     - Inbound: `VetUseCase` (list all vets with their specialties).
     - Outbound: `VetRepositoryPort`.
   - **Infrastructure (Adapters):**
     - Inbound Web: `VetRestController`, `VetResponseDto`, `SpecialtyDto`.
     - Outbound Persistence: `JpaVetRepository`.

3. **`visits` (Pet Consultations):**
   - **Domain:**
     - `Visit`: Date, description, and pet identifier.
   - **Application (Ports):**
     - Inbound: `VisitUseCase` (schedule new visit for a pet, list visits by pet).
     - Outbound: `VisitRepositoryPort`, `PetValidationPort` (validates pet existence).
   - **Infrastructure (Adapters):**
     - Inbound Web: `VisitRestController`, `VisitRequestDto`, `VisitResponseDto`.
     - Outbound Persistence: `JpaVisitRepository`.

4. **`common` (Cross-Cutting Concerns):**
   - Global exception handling (`GlobalExceptionHandler` returning RFC 7807 `ProblemDetail`).
   - OpenAPI 3.0 configuration (`OpenApiConfig`) exposing Swagger UI at `/swagger-ui.html`.
   - Shared validation constraints, base entities, and logging filters.

---

## 3. Technology Stack & Runtime Upgrades

- **Java Runtime & SDK:** Java 21 LTS (configured in `pom.xml` via `<java.version>21</java.version>`).
- **Framework:** Spring Boot 3.4.x / 4.x compatible parent.
- **REST & Documentation:** `springdoc-openapi-starter-webmvc-ui` (v2.8.x) for OpenAPI 3 and Swagger UI.
- **Persistence:** Spring Data JPA with Hibernate, supporting H2 (default), MySQL, and PostgreSQL profiles.
- **Validation:** `spring-boot-starter-validation` (Jakarta Validation / Hibernate Validator).
- **Testing:** JUnit 5, AssertJ, Mockito, Spring Boot Test (`MockMvc`).

---

## 4. REST API Specification

All endpoints are prefixed with `/api/v1` and return JSON with standard HTTP status codes:

### 4.1 Owners & Pets (`/api/v1/owners`, `/api/v1/pets`)
- `GET /api/v1/owners?lastName={lastName}&page={page}&size={size}`: Search owners with pagination (`200 OK`).
- `GET /api/v1/owners/{id}`: Get owner details including pets and visits (`200 OK`, `404 Not Found`).
- `POST /api/v1/owners`: Create a new owner (`201 Created`, `400 Bad Request`).
- `PUT /api/v1/owners/{id}`: Update an existing owner (`200 OK`, `400 Bad Request`, `404 Not Found`).
- `GET /api/v1/pet-types`: Get list of all pet types (`200 OK`).
- `POST /api/v1/owners/{ownerId}/pets`: Add a new pet to an owner (`201 Created`, `400 Bad Request`, `404 Not Found`).
- `PUT /api/v1/owners/{ownerId}/pets/{petId}`: Update an existing pet (`200 OK`, `400 Bad Request`, `404 Not Found`).

### 4.2 Vets (`/api/v1/vets`)
- `GET /api/v1/vets`: List all veterinarians with specialties (`200 OK`).

### 4.3 Visits (`/api/v1/owners/{ownerId}/pets/{petId}/visits`)
- `GET /api/v1/owners/{ownerId}/pets/{petId}/visits`: List visits for a pet (`200 OK`, `404 Not Found`).
- `POST /api/v1/owners/{ownerId}/pets/{petId}/visits`: Book a new visit (`201 Created`, `400 Bad Request`, `404 Not Found`).

---

## 5. Error Handling & Data Validation

- **Standard Response:** Every API error returns an RFC 7807 `ProblemDetail` or structured JSON containing:
  - `timestamp`: ISO-8601 date-time.
  - `status`: HTTP status code (e.g. 400, 404, 409, 500).
  - `error`: Error title/classification.
  - `message`: Specific message or validation field error list.
  - `path`: Requested URI.
- **Input Validation:**
  - Mandatory fields (`@NotBlank`, `@NotNull`).
  - Telephone formatting (`@Digits(fraction = 0, integer = 10)`).
  - String length bounds matching database columns (`@Size`).
  - Visit dates must not be in the future or invalid format.

---

## 6. Automated Testing Strategy

1. **Domain Unit Tests:** Pure JUnit 5 testing business rules on entities and value objects without Spring context.
2. **Use Case / Service Tests:** Mockito-based tests verifying business workflows and port coordination.
3. **Web API Integration Tests:** `MockMvc` tests verifying:
   - JSON serialization and deserialization.
   - Bean validation constraints returning HTTP 400.
   - Proper HTTP 200, 201, 204, 404 status codes.
   - OpenAPI spec generation availability (`/v3/api-docs`).
4. **Database Integration Tests:** Repository tests running against H2 in-memory database.

---

## 7. GitFlow Strategy & Commit Conventions

- **Branch Structure:**
  - `main`: Production-ready branch. Currently holds `Initial commit: import spring petclinic base project`.
  - `develop`: Integration branch where all completed features merge.
  - `feature/java21-upgrade`: Migration of Maven compiler, plugins, and Java 21 compatibility.
  - `feature/modular-architecture`: Hexagonal packages, domain, ports, adapters.
  - `feature/rest-api-openapi`: REST Controllers, DTOs, OpenAPI/Swagger UI setup.
  - `feature/automated-tests`: Full automated test suite (Unit & MockMvc).
  - `feature/docs-and-ai-report`: Comprehensive `README.md` and `AI_USAGE_REPORT.md`.
- **Commit Rules:**
  - Language: Strictly English.
  - Tone: Natural, concise, human, no pompous or artificial vocabulary.
  - Examples:
    - `switch project to java 21 and update plugins`
    - `restructure project into hexagonal packages`
    - `add rest controller and dtos for owners`
    - `add swagger documentation to vet endpoints`
    - `add integration tests for owner api`
    - `document project setup and ai tool usage`

---

## 8. Documentation Deliverables

1. **`README.md`:**
   - Architecture overview with Hexagonal diagram.
   - Prerequisites (Java 21, Maven).
   - How to build (`./mvnw clean package`) and run (`./mvnw spring-boot:run`).
   - Swagger UI URL (`http://localhost:8080/swagger-ui.html`) and sample cURL requests.
   - Database profile activation (H2, MySQL, PostgreSQL).
   - GitFlow workflow explanation.
2. **`AI_USAGE_REPORT.md`:**
   - AI tools employed during development (Antigravity with Google DeepMind agentic models).
   - Specific tasks where AI provided assistance:
     - Analyzing legacy monolithic code and extracting business boundaries.
     - Modernizing configurations to Java 21.
     - Designing hexagonal ports and adapters.
     - Generating OpenAPI schemas and JUnit 5 test suites.
   - Human validation methodology: review of architecture, compilation checks, test execution, and security review.
