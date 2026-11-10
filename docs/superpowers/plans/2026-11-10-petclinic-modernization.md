# Spring PetClinic Modernization Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Modernize Spring PetClinic into a clean, modular backend on Java 21 LTS with Hexagonal Architecture, full REST API documented via OpenAPI/Swagger, comprehensive tests, GitFlow branching, and complete documentation.

**Architecture:** Hexagonal Architecture (Ports and Adapters) splitting the monolithic application into decoupled bounded contexts (`customers`, `vets`, `visits`, `common`). Inbound adapters expose OpenAPI-documented REST controllers, application services implement inbound use cases, and outbound persistence adapters wrap Spring Data JPA repositories.

**Tech Stack:** Java 21 LTS, Spring Boot, Spring Data JPA, SpringDoc OpenAPI 3.0 / Swagger UI, Jakarta Validation, H2/MySQL/PostgreSQL, JUnit 5, AssertJ, MockMvc.

**Spec:** `docs/superpowers/specs/2026-11-10-petclinic-modernization-design.md`

## Global Constraints

- Must run and compile strictly on Java 21 LTS (`C:\Program Files\Java\jdk-21`).
- All new controllers must be RESTful (`@RestController`), returning JSON DTOs with HTTP status codes (200, 201, 204, 400, 404).
- All endpoints must be documented with OpenAPI annotations (`@Operation`, `@ApiResponse`, `@Tag`).
- All input DTOs must use Jakarta Bean Validation (`@NotBlank`, `@NotNull`, `@Size`, `@Digits`).
- Errors must use RFC 7807 `ProblemDetail` or standard JSON error structure.
- Commits must use simple, natural, human English without pompous words.
- GitFlow workflow: branch from `develop`, develop in `feature/*`, merge back to `develop`, final merge to `main`.

## Review Focus

1. Owner telephone validation: Invalid non-numeric or length != 10 digits returns 400 Bad Request with field error.
2. Pet creation with unknown Owner: Booking pet for non-existent owner ID returns 404 Not Found.
3. Visit date in the future or null: Booking a visit with invalid date returns 400 Bad Request.
4. Duplicate pet name for same owner: Returns 400 Bad Request or 409 Conflict.
5. OpenAPI spec accessibility: `/v3/api-docs` and `/swagger-ui.html` must load cleanly without runtime reflection errors.

---

### Task 1: GitFlow Setup and Java 21 Migration

**Files:**
- Modify: `backend/spring-petclinic/pom.xml`

**Interfaces:**
- Consumes: Existing Maven configuration.
- Produces: Java 21 compiler configuration and `springdoc-openapi-starter-webmvc-ui` dependency.

- [ ] **Step 1: Create feature branch**
```bash
git checkout develop
git checkout -b feature/java21-upgrade
```

- [ ] **Step 2: Update pom.xml to Java 21 and add OpenAPI dependency**
Update `<java.version>21</java.version>` and add:
```xml
<dependency>
  <groupId>org.springdoc</groupId>
  <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
  <version>2.8.5</version>
</dependency>
```

- [ ] **Step 3: Verify clean build with JDK 21**
Run:
```powershell
$env:JAVA_HOME="C:\Program Files\Java\jdk-21"
$env:Path="C:\Program Files\Java\jdk-21\bin;" + $env:Path
cd backend\spring-petclinic
.\mvnw.cmd clean compile
```
Expected: `BUILD SUCCESS`

- [ ] **Step 4: Commit and merge feature branch into develop**
```bash
git add backend/spring-petclinic/pom.xml
git commit -m "upgrade project to java 21 and add openapi dependency"
git checkout develop
git merge --no-ff feature/java21-upgrade -m "merge java 21 upgrade into develop"
git branch -d feature/java21-upgrade
```

---

### Task 2: Vets Bounded Context - Domain, Ports & Application Service

**Files:**
- Create: `backend/spring-petclinic/src/main/java/org/springframework/samples/petclinic/vet/domain/SpecialtyModel.java`
- Create: `backend/spring-petclinic/src/main/java/org/springframework/samples/petclinic/vet/domain/VetModel.java`
- Create: `backend/spring-petclinic/src/main/java/org/springframework/samples/petclinic/vet/port/in/GetVetsUseCase.java`
- Create: `backend/spring-petclinic/src/main/java/org/springframework/samples/petclinic/vet/port/out/VetRepositoryPort.java`
- Create: `backend/spring-petclinic/src/main/java/org/springframework/samples/petclinic/vet/service/VetService.java`
- Test: `backend/spring-petclinic/src/test/java/org/springframework/samples/petclinic/vet/service/VetServiceTest.java`

**Interfaces:**
- Consumes: None.
- Produces: `GetVetsUseCase.getAllVets(): Collection<VetModel>`, `VetRepositoryPort.findAll(): Collection<VetModel>`.

- [ ] **Step 1: Create feature branch**
```bash
git checkout develop
git checkout -b feature/vets-module
```

- [ ] **Step 2: Write failing unit test for VetService**
```java
package org.springframework.samples.petclinic.vet.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.vet.domain.SpecialtyModel;
import org.springframework.samples.petclinic.vet.domain.VetModel;
import org.springframework.samples.petclinic.vet.port.out.VetRepositoryPort;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VetServiceTest {

    @Mock
    private VetRepositoryPort vetRepositoryPort;

    @InjectMocks
    private VetService vetService;

    @Test
    void shouldReturnAllVets() {
        VetModel vet = new VetModel(1, "James", "Carter", Set.of(new SpecialtyModel(1, "radiology")));
        when(vetRepositoryPort.findAll()).thenReturn(List.of(vet));

        List<VetModel> result = vetService.getAllVets();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFirstName()).isEqualTo("James");
    }
}
```

- [ ] **Step 3: Run test to verify it fails**
Run: `.\mvnw.cmd test -Dtest=VetServiceTest`
Expected: Compilation failure (classes not yet created).

- [ ] **Step 4: Implement Domain, Ports and Service**
Create `SpecialtyModel`, `VetModel`, `GetVetsUseCase`, `VetRepositoryPort`, and `VetService`.

- [ ] **Step 5: Run test to verify it passes**
Run: `.\mvnw.cmd test -Dtest=VetServiceTest`
Expected: `BUILD SUCCESS` (1 test passed).

- [ ] **Step 6: Commit**
```bash
git add backend/spring-petclinic/src/
git commit -m "add domain models, ports and service for vets"
```

---

### Task 3: Vets REST Adapter, OpenAPI Documentation & Integration Tests

**Files:**
- Create: `backend/spring-petclinic/src/main/java/org/springframework/samples/petclinic/vet/adapter/in/web/VetRestController.java`
- Create: `backend/spring-petclinic/src/main/java/org/springframework/samples/petclinic/vet/adapter/in/web/dto/VetResponseDto.java`
- Create: `backend/spring-petclinic/src/main/java/org/springframework/samples/petclinic/vet/adapter/in/web/dto/SpecialtyResponseDto.java`
- Create: `backend/spring-petclinic/src/main/java/org/springframework/samples/petclinic/vet/adapter/out/persistence/VetPersistenceAdapter.java`
- Test: `backend/spring-petclinic/src/test/java/org/springframework/samples/petclinic/vet/adapter/in/web/VetRestControllerTest.java`

**Interfaces:**
- Consumes: `GetVetsUseCase`.
- Produces: `GET /api/v1/vets` endpoint returning JSON array of `VetResponseDto`.

- [ ] **Step 1: Write failing MockMvc test for VetRestController**
```java
package org.springframework.samples.petclinic.vet.adapter.in.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.vet.domain.SpecialtyModel;
import org.springframework.samples.petclinic.vet.domain.VetModel;
import org.springframework.samples.petclinic.vet.port.in.GetVetsUseCase;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VetRestController.class)
class VetRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GetVetsUseCase getVetsUseCase;

    @Test
    void shouldReturnVetsListAsJson() throws Exception {
        VetModel vet = new VetModel(1, "Helen", "Leary", Set.of(new SpecialtyModel(1, "radiology")));
        when(getVetsUseCase.getAllVets()).thenReturn(List.of(vet));

        mockMvc.perform(get("/api/v1/vets").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].firstName").value("Helen"))
            .andExpect(jsonPath("$[0].specialties[0].name").value("radiology"));
    }
}
```

- [ ] **Step 2: Implement DTOs, VetRestController with OpenAPI, and Persistence Adapter**
Add `@RestController`, `@RequestMapping("/api/v1/vets")`, `@Tag(name = "Veterinarians")`, `@Operation(summary = "Get list of all veterinarians")`.
Implement `VetPersistenceAdapter` bridging existing JPA `VetRepository` to `VetRepositoryPort`.

- [ ] **Step 3: Run integration test and verify it passes**
Run: `.\mvnw.cmd test -Dtest=VetRestControllerTest`
Expected: `BUILD SUCCESS`

- [ ] **Step 4: Commit and merge feature branch into develop**
```bash
git add backend/spring-petclinic/src/
git commit -m "add rest controller and openapi docs for veterinarians"
git checkout develop
git merge --no-ff feature/vets-module -m "merge vets module into develop"
git branch -d feature/vets-module
```

---

### Task 4: Customers Bounded Context (Owners & Pets) - Domain, Ports & Services

**Files:**
- Create: `backend/spring-petclinic/src/main/java/org/springframework/samples/petclinic/customer/domain/OwnerModel.java`
- Create: `backend/spring-petclinic/src/main/java/org/springframework/samples/petclinic/customer/domain/PetModel.java`
- Create: `backend/spring-petclinic/src/main/java/org/springframework/samples/petclinic/customer/domain/PetTypeModel.java`
- Create: `backend/spring-petclinic/src/main/java/org/springframework/samples/petclinic/customer/port/in/OwnerUseCase.java`
- Create: `backend/spring-petclinic/src/main/java/org/springframework/samples/petclinic/customer/port/in/PetUseCase.java`
- Create: `backend/spring-petclinic/src/main/java/org/springframework/samples/petclinic/customer/port/out/OwnerRepositoryPort.java`
- Create: `backend/spring-petclinic/src/main/java/org/springframework/samples/petclinic/customer/port/out/PetRepositoryPort.java`
- Create: `backend/spring-petclinic/src/main/java/org/springframework/samples/petclinic/customer/service/OwnerService.java`
- Create: `backend/spring-petclinic/src/main/java/org/springframework/samples/petclinic/customer/service/PetService.java`
- Test: `backend/spring-petclinic/src/test/java/org/springframework/samples/petclinic/customer/service/OwnerServiceTest.java`

**Interfaces:**
- Consumes: None.
- Produces: `OwnerUseCase`, `PetUseCase`, `OwnerRepositoryPort`, `PetRepositoryPort`.

- [ ] **Step 1: Create feature branch**
```bash
git checkout develop
git checkout -b feature/customers-module
```

- [ ] **Step 2: Write failing unit test for OwnerService**
```java
package org.springframework.samples.petclinic.customer.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.customer.domain.OwnerModel;
import org.springframework.samples.petclinic.customer.port.out.OwnerRepositoryPort;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OwnerServiceTest {

    @Mock
    private OwnerRepositoryPort ownerRepositoryPort;

    @InjectMocks
    private OwnerService ownerService;

    @Test
    void shouldFindOwnerById() {
        OwnerModel owner = new OwnerModel(1, "George", "Franklin", "110 W. Liberty St.", "Madison", "6085551023");
        when(ownerRepositoryPort.findById(1)).thenReturn(Optional.of(owner));

        Optional<OwnerModel> result = ownerService.findById(1);

        assertThat(result).isPresent();
        assertThat(result.get().getFirstName()).isEqualTo("George");
    }
}
```

- [ ] **Step 3: Implement Domain Models, Ports and Services**
Implement `OwnerModel`, `PetModel`, `PetTypeModel`, `OwnerUseCase`, `PetUseCase`, `OwnerService`, and `PetService`.

- [ ] **Step 4: Run test and verify it passes**
Run: `.\mvnw.cmd test -Dtest=OwnerServiceTest`
Expected: `BUILD SUCCESS`

- [ ] **Step 5: Commit**
```bash
git add backend/spring-petclinic/src/
git commit -m "add domain models, ports and services for owners and pets"
```

---

### Task 5: Customers REST Adapters (Owners, Pets, PetTypes) and OpenAPI

**Files:**
- Create: `backend/spring-petclinic/src/main/java/org/springframework/samples/petclinic/customer/adapter/in/web/OwnerRestController.java`
- Create: `backend/spring-petclinic/src/main/java/org/springframework/samples/petclinic/customer/adapter/in/web/PetRestController.java`
- Create: `backend/spring-petclinic/src/main/java/org/springframework/samples/petclinic/customer/adapter/in/web/dto/OwnerRequestDto.java`
- Create: `backend/spring-petclinic/src/main/java/org/springframework/samples/petclinic/customer/adapter/in/web/dto/OwnerResponseDto.java`
- Create: `backend/spring-petclinic/src/main/java/org/springframework/samples/petclinic/customer/adapter/in/web/dto/PetRequestDto.java`
- Create: `backend/spring-petclinic/src/main/java/org/springframework/samples/petclinic/customer/adapter/in/web/dto/PetResponseDto.java`
- Create: `backend/spring-petclinic/src/main/java/org/springframework/samples/petclinic/customer/adapter/in/web/dto/PetTypeDto.java`
- Create: `backend/spring-petclinic/src/main/java/org/springframework/samples/petclinic/customer/adapter/out/persistence/OwnerPersistenceAdapter.java`
- Create: `backend/spring-petclinic/src/main/java/org/springframework/samples/petclinic/customer/adapter/out/persistence/PetPersistenceAdapter.java`
- Test: `backend/spring-petclinic/src/test/java/org/springframework/samples/petclinic/customer/adapter/in/web/OwnerRestControllerTest.java`
- Test: `backend/spring-petclinic/src/test/java/org/springframework/samples/petclinic/customer/adapter/in/web/PetRestControllerTest.java`

**Interfaces:**
- Consumes: `OwnerUseCase`, `PetUseCase`.
- Produces:
  - `GET /api/v1/owners`
  - `GET /api/v1/owners/{id}`
  - `POST /api/v1/owners`
  - `PUT /api/v1/owners/{id}`
  - `GET /api/v1/pet-types`
  - `POST /api/v1/owners/{ownerId}/pets`
  - `PUT /api/v1/owners/{ownerId}/pets/{petId}`

- [ ] **Step 1: Write MockMvc tests for OwnerRestController CRUD and validation**
Verify GET owner by ID, POST owner with invalid telephone (expect 400), and PUT owner.

- [ ] **Step 2: Implement DTOs with Bean Validation, REST Controllers, and Persistence Adapters**
Annotate DTOs with `@NotBlank`, `@Size(max = 255)`, `@Digits(fraction = 0, integer = 10)`.
Implement endpoints with OpenAPI `@Tag(name = "Owners")` and `@Tag(name = "Pets")`.

- [ ] **Step 3: Run tests and verify they pass**
Run: `.\mvnw.cmd test -Dtest=OwnerRestControllerTest,PetRestControllerTest`
Expected: `BUILD SUCCESS`

- [ ] **Step 4: Commit and merge feature branch into develop**
```bash
git add backend/spring-petclinic/src/
git commit -m "add rest controllers and validation for owners and pets"
git checkout develop
git merge --no-ff feature/customers-module -m "merge customers module into develop"
git branch -d feature/customers-module
```

---

### Task 6: Visits Bounded Context - Domain, Ports, REST Adapter & Tests

**Files:**
- Create: `backend/spring-petclinic/src/main/java/org/springframework/samples/petclinic/visit/domain/VisitModel.java`
- Create: `backend/spring-petclinic/src/main/java/org/springframework/samples/petclinic/visit/port/in/VisitUseCase.java`
- Create: `backend/spring-petclinic/src/main/java/org/springframework/samples/petclinic/visit/port/out/VisitRepositoryPort.java`
- Create: `backend/spring-petclinic/src/main/java/org/springframework/samples/petclinic/visit/service/VisitService.java`
- Create: `backend/spring-petclinic/src/main/java/org/springframework/samples/petclinic/visit/adapter/in/web/VisitRestController.java`
- Create: `backend/spring-petclinic/src/main/java/org/springframework/samples/petclinic/visit/adapter/in/web/dto/VisitRequestDto.java`
- Create: `backend/spring-petclinic/src/main/java/org/springframework/samples/petclinic/visit/adapter/in/web/dto/VisitResponseDto.java`
- Create: `backend/spring-petclinic/src/main/java/org/springframework/samples/petclinic/visit/adapter/out/persistence/VisitPersistenceAdapter.java`
- Test: `backend/spring-petclinic/src/test/java/org/springframework/samples/petclinic/visit/adapter/in/web/VisitRestControllerTest.java`

**Interfaces:**
- Consumes: None.
- Produces:
  - `GET /api/v1/owners/{ownerId}/pets/{petId}/visits`
  - `POST /api/v1/owners/{ownerId}/pets/{petId}/visits`

- [ ] **Step 1: Create feature branch**
```bash
git checkout develop
git checkout -b feature/visits-module
```

- [ ] **Step 2: Write failing MockMvc test for VisitRestController**
Test creating a visit with valid description and date, and test validation rejection for empty description.

- [ ] **Step 3: Implement Domain, Ports, Service, DTOs, and REST Controller**
Include OpenAPI annotations and Bean Validation (`@NotNull LocalDate date`, `@NotBlank @Size(max = 255) String description`).

- [ ] **Step 4: Run test and verify it passes**
Run: `.\mvnw.cmd test -Dtest=VisitRestControllerTest`
Expected: `BUILD SUCCESS`

- [ ] **Step 5: Commit and merge feature branch into develop**
```bash
git add backend/spring-petclinic/src/
git commit -m "add visit domain, service and rest controller"
git checkout develop
git merge --no-ff feature/visits-module -m "merge visits module into develop"
git branch -d feature/visits-module
```

---

### Task 7: Cross-Cutting Concerns: Global Error Handling, OpenAPI Configuration & Actuator

**Files:**
- Create: `backend/spring-petclinic/src/main/java/org/springframework/samples/petclinic/common/config/OpenApiConfig.java`
- Create: `backend/spring-petclinic/src/main/java/org/springframework/samples/petclinic/common/exception/GlobalExceptionHandler.java`
- Create: `backend/spring-petclinic/src/main/java/org/springframework/samples/petclinic/common/exception/ResourceNotFoundException.java`
- Test: `backend/spring-petclinic/src/test/java/org/springframework/samples/petclinic/common/exception/GlobalExceptionHandlerTest.java`

**Interfaces:**
- Consumes: Spring Web MVC exception resolution.
- Produces: Standard RFC 7807 `ProblemDetail` responses and OpenAPI 3.0 info configuration.

- [ ] **Step 1: Create feature branch**
```bash
git checkout develop
git checkout -b feature/cross-cutting-concerns
```

- [ ] **Step 2: Implement GlobalExceptionHandler and OpenApiConfig**
Configure `@RestControllerAdvice` catching `ResourceNotFoundException` (404), `MethodArgumentNotValidException` (400), and unhandled `Exception` (500).
Configure `OpenAPI` bean with Title "Spring PetClinic Modernized REST API", Version "1.0.0", and Description.

- [ ] **Step 3: Write and run integration test for exception handling**
Run: `.\mvnw.cmd test -Dtest=GlobalExceptionHandlerTest`
Expected: `BUILD SUCCESS`

- [ ] **Step 4: Commit and merge feature branch into develop**
```bash
git add backend/spring-petclinic/src/
git commit -m "add global exception handler and openapi configuration"
git checkout develop
git merge --no-ff feature/cross-cutting-concerns -m "merge cross cutting concerns into develop"
git branch -d feature/cross-cutting-concerns
```

---

### Task 8: End-to-End Suite Verification & Legacy Compatibility

**Files:**
- Modify/Verify: `backend/spring-petclinic/src/test/java/org/springframework/samples/petclinic/*`

**Interfaces:**
- Consumes: All modules and endpoints.
- Produces: 100% clean test execution under JDK 21.

- [ ] **Step 1: Create feature branch**
```bash
git checkout develop
git checkout -b feature/automated-tests
```

- [ ] **Step 2: Execute full test suite with JDK 21**
Run:
```powershell
$env:JAVA_HOME="C:\Program Files\Java\jdk-21"
$env:Path="C:\Program Files\Java\jdk-21\bin;" + $env:Path
cd backend\spring-petclinic
.\mvnw.cmd test
```

- [ ] **Step 3: Fix any legacy or broken tests to ensure 100% pass rate**
Address any deprecations or issues arising from Java 21 / Spring Boot modernization.

- [ ] **Step 4: Commit and merge feature branch into develop**
```bash
git add backend/spring-petclinic/
git commit -m "verify all unit and integration tests pass"
git checkout develop
git merge --no-ff feature/automated-tests -m "merge automated tests verification into develop"
git branch -d feature/automated-tests
```

---

### Task 9: Technical Documentation and AI Usage Report

**Files:**
- Create: `README.md` (root repository documentation)
- Create: `AI_USAGE_REPORT.md` (root AI tools usage and methodology report)

**Interfaces:**
- Consumes: Completed architecture and test results.
- Produces: Comprehensive technical documentation for evaluators.

- [ ] **Step 1: Create feature branch**
```bash
git checkout develop
git checkout -b feature/documentation
```

- [ ] **Step 2: Create root README.md**
Include:
- Project overview & modernization highlights.
- Hexagonal architecture diagram and module explanation.
- Prerequisites (Java 21, Maven).
- How to build (`mvn clean package`) and run (`mvn spring-boot:run`).
- How to access Swagger UI (`http://localhost:8080/swagger-ui.html`).
- Complete API endpoints table with sample cURL commands.
- GitFlow strategy details.

- [ ] **Step 3: Create root AI_USAGE_REPORT.md**
Document:
- AI tools employed (Antigravity with Google DeepMind reasoning models).
- Specific assistance areas: code exploration, Java 21 migration, hexagonal boundary extraction, OpenAPI annotations, automated test generation.
- Validation protocol: human oversight, compilation checks, test execution.

- [ ] **Step 4: Commit and merge feature branch into develop**
```bash
git add README.md AI_USAGE_REPORT.md
git commit -m "add comprehensive documentation and ai usage report"
git checkout develop
git merge --no-ff feature/documentation -m "merge documentation into develop"
git branch -d feature/documentation
```

---

### Task 10: GitFlow Final Release to main

**Files:**
- All repository files.

**Interfaces:**
- Consumes: Fully verified `develop` branch.
- Produces: Production-ready `main` branch tagged `v1.0.0`.

- [ ] **Step 1: Checkout main and merge develop**
```bash
git checkout main
git merge --no-ff develop -m "release version 1.0.0 with modernized modular architecture"
```

- [ ] **Step 2: Tag release v1.0.0**
```bash
git tag -a v1.0.0 -m "Release v1.0.0: Modernized Spring PetClinic with Java 21 and Hexagonal Architecture"
```

- [ ] **Step 3: Verify git log history**
```bash
git log --graph --oneline -n 20
```
Expected: Clean GitFlow history showing feature branches merged into `develop` and final release to `main`.
