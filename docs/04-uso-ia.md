# Bitácora de Uso y Validación de Inteligencia Artificial

## 1. Herramientas y Modelos Utilizados

- **Herramienta principal**: Antigravity (Asistente de ingeniería de software con capacidades de ejecución de comandos, lectura de archivos y análisis de código).
- **Modelo de razonamiento**: Modelos de lenguaje avanzados con soporte para inspección y edición de código fuente.
- **Entorno de trabajo**: VS Code / Antigravity IDE sobre sistema operativo Windows, con terminal PowerShell y JDK 21 LTS.

---

## 2. Objetivos de Cada Uso Relevante

El asistente de IA se utilizó de forma guiada en fases específicas del proyecto:

1. **Diagnóstico y análisis inicial**: Inspección automática de clases del proyecto original, búsqueda de anotaciones JPA, validaciones de Bean Validation y dependencias de Maven.
2. **Diseño arquitectónico**: Discusión de alternativas de diseño (Monolito modular con Arquitectura Hexagonal vs. Microservicios independientes).
3. **Migración a Java 21**: Identificación de dependencias compatibles con Java 21 y actualización del archivo `pom.xml`.
4. **Implementación de código repetitivo**: Generación de esqueletos de DTOs, modelos de dominio, puertos de entrada/salida y controladores REST.
5. **Generación y ajuste de pruebas**: Creación de pruebas unitarias y de integración para controladores con `MockMvc`.
6. **Resolución de problemas técnicos**: Diagnóstico de fallos en la ejecución de pruebas con Testcontainers y Docker Compose.

---

## 3. Prompts Representativos e Instrucciones

A continuación se resumen las instrucciones clave dadas a la herramienta durante el proceso:

- **Análisis de reglas de negocio**:
  > *"Revisar el código existente de PetClinic, identificar dependencias, validaciones y reglas de negocio de los dueños, mascotas y visitas."*
- **Estrategia de arquitectura**:
  > *"Plantear opciones de arquitectura: microservicios o arquitectura hexagonal. Procederemos con la Opción B (Monolito modular hexagonal)."*
- **Implementación paso a paso**:
  > *"Crear el módulo de veterinarios con dominio, puertos de entrada y salida, servicio, controlador REST con OpenAPI y pruebas."*
- **Criterio de commits**:
  > *"Hacer commits desde la fase inicial y por cada módulo desarrollado, con mensajes en inglés natural y humano, sin palabras rebuscadas ni rimbombantes, siguiendo GitFlow."*
- **Alineación con la rúbrica técnica**:
  > *"Revisar el documento PRUEBA_TECNICA_JAVA_PETCLINIC.pdf y ajustar la documentación para cumplir con todos los entregables requeridos de forma clara."*

---

## 4. Sugerencias Aceptadas, Rechazadas y Corregidas

### 4.1. Sugerencias Aceptadas
- **Adopción de Arquitectura Hexagonal**: La propuesta de separar `domain`, `port/in`, `port/out`, `service` y `adapter` permitió desacoplar totalmente la lógica de negocio de Spring y de JPA.
- **Uso de ProblemDetail (RFC 7807)**: La sugerencia de estandarizar las respuestas de error HTTP bajo el formato nativo de Spring Boot facilitó un contrato de API consistente para clientes REST.

### 4.2. Sugerencias Rechazadas
- **División prematura en microservicios independientes**: La IA inicialmente planteó separar la aplicación en tres proyectos y procesos independientes (`vets-service`, `customers-service`, `visits-service`). Se rechazó esta opción debido a que introduciría sobrecarga innecesaria de red, requeriría bases de datos independientes con transacciones distribuidas y excedía el tiempo razonable de entrega de 1 día. Se optó por el monolito modular.

### 4.3. Sugerencias Corregidas por Criterio Humano
- **Gestión de entidades anidadas (`Pet` y `Visit`)**: La IA propuso crear repositorios directos `PetRepository` y `VisitRepository` con `@Entity` independientes. Al revisar el código original, se constató que `Pet` y `Visit` se gestionaban exclusivamente mediante cascada desde `Owner`. La propuesta se corrigió para que los adaptadores de persistencia delegaran en `OwnerRepository`, evitando inconsistencias de base de datos.
- **Tono de la documentación y commits**: Se corrigieron descripciones iniciales que sonaban artificialmente complejas o exageradas, reemplazándolas por un lenguaje técnico directo, claro y natural de desarrollador.

---

## 5. Validación Técnica de Resultados

Ningún bloque de código generado por IA fue aceptado sin validación técnica mediante el siguiente protocolo:

1. **Revisión de código línea por línea**: Se comprobó que los DTOs no expusieran entidades JPA y que las validaciones (`@Future`, `@NotBlank`, `@Pattern`) coincidieran con las reglas legacy.
2. **Formato de código automático**: Se ejecutó `./mvnw spring-javaformat:apply` para asegurar que el código cumpliera las directrices oficiales de Spring.
3. **Compilación estricta con JDK 21**: Validación de que el código compilara sin advertencias de deprecación usando el compilador de Java 21 (`javac release 21`).
4. **Ejecución de suite de pruebas automatizadas**: Ejecución de `./mvnw test`, validando que las 100 pruebas (unitarias, integración, controladores y bases de datos reales en contenedores) pasaran al 100% (0 fallos, 0 errores).

---

## 6. Limitaciones y Errores de IA Detectados

Durante el desarrollo se detectaron errores en las respuestas de la IA que requirieron intervención humana:

1. **Uso de API obsoleta de test (`@MockBean`)**:
   - *Error*: La IA intentó utilizar `@MockBean` de `org.springframework.boot.test.mock.mockito`. En las versiones modernas de Spring Boot (4.x / Spring Framework 6.2+), esta anotación fue reemplazada por `@MockitoBean` en `org.springframework.test.context.bean.override.mockito`.
   - *Corrección*: Se actualizó el código de prueba para usar `@MockitoBean`.
2. **Falta de importación del manejador de errores en slices de test**:
   - *Error*: Al generar pruebas con `@WebMvcTest(OwnerRestController.class)`, la IA no incluyó el `@Import(GlobalExceptionHandler.class)`. Como resultado, los tests que esperaban error 404 recibían 500 porque MockMvc no cargaba el manejador global.
   - *Corrección*: Se agregó `@Import(GlobalExceptionHandler.class)` en todos los tests de controladores web.
3. **Conflicto de puertos en Docker Compose**:
   - *Error*: La prueba `PostgresIntegrationTests` fallaba en la máquina local porque la IA intentaba levantar el contenedor en el puerto `5432:5432`, donde ya existía un servicio local de PostgreSQL en ejecución en Windows.
   - *Corrección*: Se identificó el conflicto mediante `netstat` y se reconfiguró el puerto en `docker-compose.yml` a `5433:5432`, actualizando la propiedad `spring.datasource.url` correspondiente.

---

## 7. Conclusión

El uso de Inteligencia Artificial como herramienta de asistencia técnica permitió agilizar tareas mecánicas (escritura de DTOs, mapeos repetitivos y plantillas de prueba). Sin embargo, el criterio técnico del desarrollador, el diseño de la arquitectura y la validación rigurosa mediante pruebas automatizadas fueron indispensables para garantizar una solución correcta, mantenible y libre de errores.
