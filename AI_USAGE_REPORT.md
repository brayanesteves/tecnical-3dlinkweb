# Artificial Intelligence Usage Report

## Modernization of Spring PetClinic Backend

---

## 1. Executive Summary

This report documents the systematic application of Artificial Intelligence (AI) throughout the architectural analysis, technological modernization, and engineering implementation of the **Spring PetClinic** backend solution.

The primary objective of this exercise was to transform an existing Java monolith into a clean, modern, and maintainable architecture compliant with **Hexagonal Architecture (Ports and Adapters)** standards, migrated to **Java 21 LTS**, exposed via documented **OpenAPI 3.0 REST services**, and fully verified with automated test suites under **GitFlow** branching governance.

All stages were executed using **Google Antigravity**, an agentic software engineering AI designed for advanced pair programming and autonomous task execution.

---

## 2. AI Tooling Profile

- **AI Agent**: Antigravity (Advanced Agentic AI Assistant, Google DeepMind team)
- **Role**: Backend Engineering Co-Pilot & Autonomous Implementer
- **Supervision Model**: Human-in-the-Loop (HITL) paired programming
- **Methodology Applied**:
  - Spec-Driven Development (SDD)
  - Test-Driven Development (TDD)
  - Systematic Debugging Protocol
  - Evidence-First Verification

---

## 3. Engineering Phases and AI Application

```
+---------------------------------------------------------------------------------------+
|                               AI-ASSISTED WORKFLOW LIFECYCLE                          |
+---------------------------------------------------------------------------------------+
|  1. Exploration & Analysis     --> Reverse-engineered business rules and JPA nuances  |
|  2. Architectural Trade-offs   --> Formulated Options A & B; User confirmed Option B   |
|  3. Java 21 Migration          --> Managed POM updates, compiler flags, and format    |
|  4. Hexagonal Implementation   --> Built Bounded Contexts: Vets, Customers, Visits    |
|  5. REST & OpenAPI Contract    --> Generated DTOs with Bean Validation & Swagger UI   |
|  6. Systematic Debugging       --> Diagnosed Port 5432 collision & Exception Handler   |
|  7. Test Verification          --> 100% test pass rate across 100 automated tests     |
+---------------------------------------------------------------------------------------+
```

### Phase 1: Codebase Analysis and Reverse Engineering
- **Action**: The AI performed an automated deep-scan of the existing PetClinic codebase, cataloging domain entities, JPA relationship mappings, and validation constraints.
- **Key Discovery**: Identified that `Pet` and `Visit` entities are managed via cascading from `Owner` (rather than standalone repositories), preventing regression when designing independent domain models.
- **AI Tool Output**: Produced a structured architectural analysis identifying boundary lines between Vets, Customers (Owners & Pets), and Visits.

### Phase 2: Architectural Alignment & Strategy Formulation
- **Action**: Formulated two concrete modernization paths for user decision:
  - **Option A**: Independent Microservices with separate deployment units.
  - **Option B**: Hexagonal Architecture (Ports & Adapters) inside a clean Modular Monolith.
- **Human Decision**: The user chose **Option B**, ensuring domain isolation without introducing distributed transaction overhead.
- **Plan Generation**: Authored a detailed 10-step implementation plan with strict GitFlow branch tracking.

### Phase 3: Technological Migration to Java 21 LTS
- **Action**: Updated `pom.xml` to `<java.version>21</java.version>` and integrated `springdoc-openapi-starter-webmvc-ui` (v2.8.5).
- **Tooling Adaptation**: Discovered that Spring Boot 4.x / modern Spring Framework versions moved mock injection from `@MockBean` to `@MockitoBean` under `org.springframework.test.context.bean.override.mockito.MockitoBean`. The AI adapted all test mocks accordingly.

### Phase 4: Modular Domain & REST Implementation
For each bounded context (`vets`, `customers`, `visits`):
1. **Domain Models**: Authored pure POJO domain objects decoupled from JPA (`VetModel`, `OwnerModel`, `PetModel`, `VisitModel`).
2. **Ports**: Defined Inbound Ports (`*UseCase`) and Outbound Ports (`*RepositoryPort`).
3. **Application Services**: Implemented business rules, including visit date future validation and owner/pet associations.
4. **Inbound Adapters**: Created REST controllers (`/api/v1/*`), request/response DTOs with Jakarta Bean Validation (`@NotNull`, `@Size`, `@Pattern`, `@Future`), and OpenAPI documentation annotations.
5. **Outbound Adapters**: Implemented persistence adapters translating between domain models and JPA entities.

### Phase 5: Systematic Root-Cause Debugging
During integration testing, two distinct failures were detected and resolved:
1. **Missing Exception Translation in Unit Tests**:
   - *Symptom*: `OwnerRestControllerTest.shouldReturn404WhenOwnerNotFound` returned HTTP 500 instead of 404.
   - *Diagnosis*: `GlobalExceptionHandler` was catching `ResponseStatusException` under generic `Exception.class` and mapping it to 500, and `@WebMvcTest` needed explicit `@Import(GlobalExceptionHandler.class)`.
   - *Resolution*: Added dedicated `ResponseStatusException` handler in `GlobalExceptionHandler` and imported it into the web test slice.
2. **PostgreSQL Port 5432 Collision in Docker Compose**:
   - *Symptom*: `PostgresIntegrationTests` failed with `FATAL: la autentificación password falló para el usuario «petclinic»`.
   - *Diagnosis*: Port 5432 was already bound to a host Windows PostgreSQL service (PID 9472) whose system language was Spanish. Connecting to `localhost:5432` routed traffic to the host instead of the container.
   - *Resolution*: Reconfigured `docker-compose.yml` and `application-postgres.properties` to map port `5433:5432`, eliminating host port collision and enabling clean test execution.

### Phase 6: Automated Verification
- Ran full test suite across all 28 test classes.
- Result: **100 tests executed, 0 failures, 0 errors, 0 skipped (100% passing)**.

---

## 4. Prompting Strategy and Human-in-the-Loop Interaction

The collaboration followed an iterative prompting methodology characterized by:

1. **Clear Architectural Constraints**:
   - User instructed: *"Haces commit desde la fase inicial y commits lo que se ha ido desarrollando, los commits en ingles sin usar palabras rebuscadas y rimbombantes, natural humano"*
   - AI Response: Maintained simple, natural commit messages (`add vets rest controller and openapi documentation`, `verify all unit and integration tests pass`).

2. **Explicit User Approval Gates**:
   - Major design choices (Option A vs Option B) were presented with clear trade-offs before generating code.
   - Proceed approvals were confirmed at key checkpoints.

3. **Incremental Verification**:
   - Every feature branch was compiled, formatted with `spring-javaformat:apply`, and tested before merging into `develop`.

---

## 5. Quantitative & Qualitative Assessment

| Dimension | Before Modernization | After AI Modernization |
| :--- | :--- | :--- |
| **Java Platform** | Java 17 | **Java 21 LTS** |
| **Architecture** | Monolithic MVC (JPA coupled to web) | **Hexagonal (Ports & Adapters)** |
| **API Contract** | Server-side HTML (Thymeleaf) | **RESTful JSON + OpenAPI 3.0 / Swagger** |
| **Validation** | Form-binding binding results | **Jakarta Bean Validation on DTOs** |
| **Error Handling** | HTML error pages | **RFC 7807 ProblemDetail JSON** |
| **Automated Tests** | 76 tests (monolithic MVC) | **100 tests (100% passing)** |
| **Version Control** | Single branch | **GitFlow (`main`, `develop`, `feature/*`)** |

---

## 6. Conclusions and Best Practices

1. **AI as an Accelerator**: The AI completed domain modeling, port/adapter isolation, validation, REST endpoints, and test generation in an fraction of standard development cycles while maintaining strict clean-code principles.
2. **The Necessity of Human Validation**: Human oversight ensured adherence to project constraints (Option B selection, commit tone, GitFlow rigor).
3. **Automated Feedback Loops**: Combining AI generation with automated Maven compilation and test execution provided rapid verification, preventing hallucinated dependencies or broken contracts.
