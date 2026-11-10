# Plan y Matriz de Migración

## 1. Alcance y Secuencia de Transformación

La migración se diseñó como un proceso incremental de menor a mayor riesgo, asegurando que cada etapa mantuviera la suite de pruebas en verde antes de avanzar a la siguiente.

```
+---------------------------------------------------------------------------------+
|                               SECUENCIA DE MIGRACIÓN                            |
+---------------------------------------------------------------------------------+
|  1. Baseline GitFlow y Diagnóstico Inicial                                      |
|  2. Actualización a Java 21 LTS y Dependencias OpenAPI                          |
|  3. Módulo Veterinarios (Vet): Dominio, Puertos, Servicio, REST y Tests         |
|  4. Módulo Clientes (Owners & Pets): Dominio, Puertos, Servicios, REST y Tests  |
|  5. Módulo Citas (Visits): Dominio, Puertos, Reglas de Fecha, REST y Tests      |
|  6. Aspectos Transversales: OpenAPI Swagger y Manejo Global de Errores          |
|  7. Verificación de Pruebas Automatizadas y Ajuste de Docker Compose            |
|  8. Documentación Técnica y Cierre de Release v1.0.0                            |
+---------------------------------------------------------------------------------+
```

---

## 2. Matriz de Correspondencia: Legacy vs. Nueva API REST

La siguiente matriz documenta la equivalencia funcional entre los controladores web originales (Thymeleaf/MVC) y los nuevos endpoints de la API REST:

| Capacidad de Negocio | Controlador Legacy | Nuevo Endpoint REST | Método HTTP | DTO Solicitud / Validaciones | DTO Respuesta | Código HTTP |
| :--- | :--- | :--- | :---: | :--- | :--- | :---: |
| **Listar veterinarios** | `VetController.showVetList` | `/api/v1/vets` | `GET` | Ninguno | `List<VetResponseDto>` | `200 OK` |
| **Registrar dueño** | `OwnerController.processCreationForm` | `/api/v1/owners` | `POST` | `OwnerRequestDto` (`@NotBlank`, `@Pattern` 10 dígitos) | `OwnerResponseDto` | `201 Created` |
| **Buscar dueños por apellido** | `OwnerController.processFindForm` | `/api/v1/owners?lastName=...` | `GET` | Parámetro `lastName` (opcional) | `List<OwnerResponseDto>` | `200 OK` |
| **Consultar dueño por ID** | `OwnerController.showOwner` | `/api/v1/owners/{ownerId}` | `GET` | Path variable `ownerId` | `OwnerResponseDto` | `200 OK` / `404 Not Found` |
| **Listar tipos de mascotas** | `PetController.populatePetTypes` | `/api/v1/owners/{ownerId}/pets/types` | `GET` | Path variable `ownerId` | `List<PetTypeDto>` | `200 OK` |
| **Agregar mascota a un dueño** | `PetController.processCreationForm` | `/api/v1/owners/{ownerId}/pets` | `POST` | `PetRequestDto` (`@NotBlank`, `@NotNull` fecha, `typeId`) | `PetResponseDto` | `201 Created` |
| **Agendar visita médica** | `VisitController.processNewVisitForm` | `/api/v1/owners/{ownerId}/pets/{petId}/visits` | `POST` | `VisitRequestDto` (`@Future`, `@NotBlank` descripción) | `VisitResponseDto` | `201 Created` / `400 Bad Request` |
| **Consultar visitas de mascota** | `OwnerController.showOwner` (vistas anidadas) | `/api/v1/owners/{ownerId}/pets/{petId}/visits` | `GET` | Path variables `ownerId`, `petId` | `List<VisitResponseDto>` | `200 OK` |

---

## 3. Preservación de Reglas de Negocio

Todas las validaciones originales fueron trasladadas a los DTOs de entrada y servicios de aplicación:

1. **Teléfono del Propietario**:
   Validado mediante `@Pattern(regexp = "^[0-9]{10}$", message = "Telephone must be exactly 10 digits")`.
2. **Fecha de Visita**:
   Validada en dos capas:
   - Anotación `@Future` en `VisitRequestDto`.
   - Validación defensiva en `VisitService.java` verificando `visitModel.getDate().isAfter(LocalDate.now())`.
3. **Mascota y Tipo**:
   `PetRequestDto` valida que el nombre no sea vacío y que el identificador `typeId` corresponda a un tipo de mascota registrado en el catálogo.

---

## 4. Gestión de Riesgos Técnicos y Mitigaciones

| Riesgo Identificado | Impacto | Mitigación Implementada |
| :--- | :--- | :--- |
| **Cascadas JPA en Pets y Visits** | Intentar crear repositorios independientes para `Pet` o `Visit` causaría errores de clave foránea porque en el modelo legacy la propiedad está en `Owner`. | Los adaptadores de persistencia (`PetPersistenceAdapter` y `VisitPersistenceAdapter`) encapsulan la operación utilizando `OwnerRepository`, preservando la integridad referencial sin modificar el esquema de base de datos. |
| **Sintaxis deprecada en tests con Java 21** | `@MockBean` ya no se encuentra en el paquete habitual de Spring Boot moderno. | Se actualizaron las pruebas a `@MockitoBean` del paquete `org.springframework.test.context.bean.override.mockito`. |
| **Manejo de excepciones en slices `@WebMvcTest`** | Al probar errores 404 en controladores REST aislados, MockMvc no cargaba el `GlobalExceptionHandler` por defecto, devolviendo 500 en lugar de 404. | Se incorporó `@Import(GlobalExceptionHandler.class)` en las clases de test web, asegurando la captura correcta y el formato RFC 7807. |
| **Conflicto de puerto 5432 en Docker Compose** | En máquinas de desarrollo con PostgreSQL local corriendo en Windows, `docker-compose.yml` fallaba al intentar enlazar `5432:5432`. | Se configuró el puerto del contenedor en `5433:5432` en `docker-compose.yml` y la URL correspondiente en `application-postgres.properties`. |
