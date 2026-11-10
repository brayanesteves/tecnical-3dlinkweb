# Diagnóstico del Código Legacy - Spring PetClinic

## 1. Introducción y Contexto

El presente documento detalla el análisis técnico y funcional realizado sobre el código fuente original de **Spring PetClinic**, previo a su modernización. El objetivo fue identificar las tecnologías utilizadas, el acoplamiento existente, las reglas de negocio clave, las validaciones y los riesgos inherentes a la transformación hacia una arquitectura modular con APIs REST.

---

## 2. Tecnologías y Versiones Identificadas

| Componente | Tecnología Original | Detalle |
| :--- | :--- | :--- |
| **Lenguaje** | Java 17 | Versión base configurada en `pom.xml` |
| **Framework Base** | Spring Boot 3.x / 4.x-SNAPSHOT | Starter web con renderizado del lado del servidor |
| **Renderizado Web** | Thymeleaf | Formularios HTML dependientes del binding de Spring MVC |
| **Capa de Datos** | Spring Data JPA / Hibernate | Mapeos relacionales con Hibernate ORM |
| **Validaciones** | Jakarta Bean Validation / Hibernate Validator | Reglas a nivel de entidades JPA |
| **Bases de Datos** | H2, MySQL, PostgreSQL | Soporte multi-base de datos mediante perfiles de Spring |
| **Contenedores** | Spring Boot Docker Compose | Archivo `docker-compose.yml` para levantar MySQL y Postgres |
| **Pruebas** | JUnit 5, Mockito, Testcontainers | Pruebas de controladores web y de integración |

---

## 3. Componentes y Flujos Funcionales Originales

El sistema original se estructura en torno a cuatro entidades principales:

### 3.1. Dueños (`Owner`) y Mascotas (`Pet`)
- **Controladores originales**: `OwnerController`, `PetController`.
- **Mapeo de datos**: La entidad `Owner` actúa como raíz del agregado. Mantiene una relación `@OneToMany` hacia `Pet` con cascada total (`CascadeType.ALL`).
- **Flujos**:
  - Búsqueda de propietarios por apellido con paginación (`findOwners`).
  - Registro de nuevos propietarios y actualización de datos de contacto.
  - Alta y edición de mascotas asociadas a un propietario específico.
  - Catálogo de tipos de mascotas mediante `PetTypeRepository` y el formateador web `PetTypeFormatter`.

### 3.2. Visitas Veterinarias (`Visit`)
- **Controlador original**: `VisitController`.
- **Mapeo de datos**: Cada `Pet` mantiene una colección de visitas (`@OneToMany` con clave foránea `pet_id`). La persistencia se realiza a través de la actualización del `Owner`.
- **Flujos**:
  - Consulta de historial de visitas en la vista de detalle del dueño.
  - Formulario de agendamiento de nuevas citas para una mascota.

### 3.3. Veterinarios (`Vet`)
- **Controlador original**: `VetController`.
- **Mapeo de datos**: Entidad `Vet` con relación `@ManyToMany` hacia `Specialty`.
- **Flujos**:
  - Listado paginado de veterinarios y sus especialidades.
  - Mecanismo de caché en memoria con Spring Cache (`@Cacheable("vets")`).
  - Soporte básico de salida XML/JSON a través de un objeto contenedor `Vets`.

---

## 4. Validaciones y Reglas de Negocio Identificadas

Durante la revisión del código se identificaron las siguientes reglas y validaciones críticas que debían preservarse obligatoriamente:

1. **Teléfono de Dueños**:
   - Debe contener exactamente 10 dígitos numéricos (`@Digits(fraction = 0, integer = 10)`).
   - No puede estar vacío (`@NotBlank`).
2. **Campos Obligatorios de Personas**:
   - `firstName` y `lastName` no pueden ser nulos ni estar vacíos (`@NotBlank`).
   - `address` y `city` son obligatorios (`@NotBlank`).
3. **Mascotas**:
   - `name` es obligatorio (`@NotBlank`).
   - `birthDate` es obligatoria (`@NotNull`) y no debe ser una fecha inválida.
   - `type` (tipo de animal) debe existir previamente en el catálogo del sistema.
4. **Visitas Veterinarias**:
   - `description` no puede estar en blanco y tiene un límite de 255 caracteres (`@NotBlank`, `@Size(max = 255)`).
   - `date` es obligatoria y **debe ser estrictamente futura**. En `VisitController.java` se valida expresamente:
     `if (visit.getDate() != null && !visit.getDate().isAfter(LocalDate.now())) { ... }`

---

## 5. Deuda Técnica y Hallazgos Arquitectónicos

1. **Acoplamiento de Entidades JPA a la Capa Web**:
   Las entidades de persistencia (`Owner`, `Pet`, `Visit`) eran utilizadas directamente como modelos de formulario en las vistas Thymeleaf y en los parámetros de los controladores. Esto exponía la estructura interna de la base de datos al cliente.
2. **Dependencia de Sesión y ModelAttribute**:
   Los controladores dependían fuertemente de métodos `@ModelAttribute` que consultaban la base de datos antes de cada petición (`loadPetWithVisit`), dificultando su reutilización como servicios REST desacoplados.
3. **Ausencia de Contratos REST Claros**:
   No existían DTOs de solicitud/respuesta independientes ni documentación OpenAPI/Swagger para el consumo por parte de clientes móviles o APIs externas.
4. **Cascada de Persistencia en Agregados**:
   `Pet` y `Visit` no poseían repositorios independientes (`PetRepository` o `VisitRepository` no existían como interfaces Spring Data directas); todas las operaciones se realizaban mutando el objeto `Owner` y guardando la entidad raíz.

---

## 6. Estrategia Frente a los Hallazgos

- **Mantener**: El esquema relacional existente (tablas `owners`, `pets`, `types`, `visits`, `vets`, `specialties`) para garantizar 100% de compatibilidad con datos existentes.
- **Modernizar**:
  - Migrar la compilación y ejecución a **Java 21 LTS**.
  - Reemplazar el acoplamiento MVC/Thymeleaf creando adaptadores REST (`/api/v1/*`) con DTOs validados.
  - Aplicar **Arquitectura Hexagonal**: modelos de dominio puros, puertos de entrada/salida y adaptadores de persistencia que encapsulen el acceso a datos.
  - Documentar todos los endpoints con **OpenAPI 3.0 / Swagger UI**.
  - Estandarizar las respuestas de error mediante **RFC 7807 (`ProblemDetail`)**.
