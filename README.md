# 🎫 Helpdesk API - Arquitectura Hexagonal Modular

Backend del sistema **Helpdesk** desarrollado con **Java 21** y **Spring Boot 4**, diseñado bajo los principios de **Arquitectura Hexagonal (Puertos y Adaptadores)** y estructurado por características o subdominios (**Package-by-Feature / Monolito Modular**).

---

## 📌 Tabla de Contenidos
- [Descripción del Proyecto](#-descripción-del-proyecto)
- [Arquitectura y Principios de Diseño](#-arquitectura-y-principios-de-diseño)
- [Estructura del Proyecto](#-estructura-del-proyecto)
- [Módulos del Sistema](#-módulos-del-sistema)
- [Stack Tecnológico](#-stack-tecnológico)
- [Configuración y Ejecución](#-configuración-y-ejecución)
- [Buenas Prácticas para Nuevos Módulos](#-buenas-prácticas-para-nuevos-módulos)

---

## 📖 Descripción del Proyecto

**Helpdesk API** es una solución backend para la administración integral de mesas de ayuda, soporte técnico y atención de tickets. Permite gestionar:
- Usuarios, roles y asignación departamental.
- Ciclo de vida completo de tickets de soporte y atención.
- Categorías, prioridades y acuerdos de nivel de servicio (SLA).
- Notificaciones y mecanismos de autenticación y autorización.

---

## 🏛️ Arquitectura y Principios de Diseño

El proyecto sigue una **Arquitectura Hexagonal Modular (*Package-by-Feature*)**, donde cada módulo de negocio representa un subdominio autocontenido con su propio hexágono independiente:

```
                  +-----------------------------------+
                  |      INFRASTRUCTURE (Adaptadores) |
                  |  +-----------------------------+  |
                  |  |    APPLICATION (Casos de uso)|  |
                  |  |  +-----------------------+  |  |
                  |  |  |   DOMAIN (Modelo puro)|  |  |
[REST Controller] ---> [Inbound Port] --> [Service] --> [Domain]  |
                  |  |        |                 |  |  |
                  |  |  +----[Outbound Port]----+  |  |
                  |  +-------------|---------------+  |
                  |                v                  |
                  |    [Persistence Adapter / JPA]    |
                  +----------------|------------------+
                                   v
                             [PostgreSQL]
```

### Capas dentro de cada módulo:

1. **`domain` (Núcleo del Dominio):**
   - **`model/`**: Entidades y modelos de negocio puros, libres de dependencias de frameworks o anotaciones JPA.
   - **`exception/`**: Excepciones específicas del subdominio (ej. `UserNotFoundException`, `DepartmentAlreadyExistsException`).

2. **`application` (Lógica de Aplicación y Casos de Uso):**
   - **`port/in/`**: Interfaces que definen los casos de uso disponibles para los actores externos (ej. `CreateUserUseCase`).
   - **`port/out/`**: Puertos de salida que definen las necesidades secundarias como persistencia o clientes externos (ej. `UserRepositoryPort`).
   - **`service/`**: Implementación de los casos de uso, orquestación de lógica y validaciones de negocio.

3. **`infrastructure` (Adaptadores e Integraciones Técnicas):**
   - **`adapter/in/rest/`**: Controladores Spring MVC REST y DTOs de solicitud/respuesta (`dto/`).
   - **`adapter/out/persistence/`**: Entidades JPA (`*Entity`), repositorios Spring Data JPA (`*Repository`), mappers (`*PersistenceMapper`) y adaptadores que implementan los puertos de salida (`*PersistenceAdapter`).

4. **`shared` (Componentes Transversales):**
   - Excepciones base reutilizables (`DomainException`, `BusinessException`, `ResourceNotFoundException`, `ResourceAlreadyExistsException`).
   - Manejador global de excepciones REST (`GlobalExceptionHandler`) y estructura estándar de errores (`ErrorResponse`).

---

## 📂 Estructura del Proyecto

```text
src/main/java/com/alexhiz/hexagonal/helpdesk/
│
├── HelpdeskApplication.java             # Clase principal Spring Boot
│
├── shared/                              # Transversal / Core compartido
│   ├── domain/
│   │   └── exception/                   # Excepciones base del dominio
│   └── infrastructure/
│       └── adapter/in/rest/
│           ├── GlobalExceptionHandler.java
│           └── dto/
│               └── ErrorResponse.java
│
├── department/                          # Subdominio de Departamentos
│   ├── domain/
│   │   ├── model/
│   │   └── exception/
│   ├── application/
│   │   ├── port/in/
│   │   ├── port/out/
│   │   └── service/
│   └── infrastructure/
│       └── adapter/
│           ├── in/rest/dto/
│           └── out/persistence/
│
├── role/                                # Subdominio de Roles
│   ├── domain/
│   ├── application/
│   └── infrastructure/
│
├── user/                                # Subdominio de Usuarios
│   ├── domain/
│   ├── application/
│   └── infrastructure/
│
├── ticket/                              # Subdominio de Tickets (Mesa de ayuda)
│   ├── domain/
│   ├── application/
│   └── infrastructure/
│
├── category/                            # Subdominio de Categorías
├── sla/                                 # Subdominio de Acuerdos de Nivel de Servicio (SLA)
├── notification/                        # Subdominio de Notificaciones
└── auth/                                # Subdominio de Autenticación y Seguridad
```

---

## 📦 Módulos del Sistema

| Módulo | Estado | Descripción |
|---|:---:|---|
| **`shared`** |  Activo | Excepciones base, manejador global de errores y utilidades transversales. |
| **`department`** |  Activo | Gestión y administración de departamentos organizacionales. |
| **`role`** |  Activo | Gestión de roles del sistema. |
| **`user`** |  Activo | Gestión de usuarios, asignación de roles y departamentos. |
| **`ticket`** | 🔨 En desarrollo | Creación, seguimiento, estados, prioridad y resolución de tickets. |
| **`category`** | 🔨 En desarrollo | Clasificación y tipos de requerimientos de soporte. |
| **`sla`** | 🔨 En desarrollo | Reglas de tiempos de primera respuesta y resolución. |
| **`notification`** | 🔨 En desarrollo | Envío de notificaciones y alertas por eventos del sistema. |
| **`auth`** | 🔨 En desarrollo | Autenticación de usuarios, tokens JWT y autorización de endpoints. |

---

## 🛠️ Stack Tecnológico

- **Lenguaje:** Java 21 (LTS)
- **Framework:** Spring Boot 4.1.0
  - Spring MVC (Web RESTful APIs)
  - Spring Data JPA
  - Spring Validation (Jakarta Validation)
- **Base de Datos:** PostgreSQL
- **Herramientas de desarrollo:** Lombok, DevTools
- **Build Tool:** Apache Maven (vía `mvnw` wrapper)

---

## 🚀 Configuración y Ejecución

### Prerrequisitos
- JDK 21 instalado y configurado en tu `JAVA_HOME`.
- Base de datos PostgreSQL en ejecución.

### 1. Clonar el repositorio
```bash
git clone <URL_DEL_REPOSITORIO>
cd helpdesk
```

### 2. Configurar Base de Datos
Actualiza `src/main/resources/application.properties` con tus credenciales de PostgreSQL:

```properties
spring.application.name=helpdesk

# PostgreSQL Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/helpdesk_db
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA / Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

### 3. Compilar y Ejecutar
```bash
# Compilar el proyecto
./mvnw clean compile

# Ejecutar la aplicación
./mvnw spring-boot:run
```

---

## 📐 Buenas Prácticas para Nuevos Módulos

1. **Aislamiento del Dominio:**
   - Nunca agregues anotaciones de frameworks (`@Entity`, `@Table`, `@RestController`, `@Autowired`) dentro de `domain/model`.
2. **Comunicación entre Módulos:**
   - Si un módulo necesita comunicarse con otro (por ejemplo, `user` consultando `department`), debe hacerlo mediante interfaces de casos de uso (`port/in` o `port/out`), nunca accediendo directamente a las entidades JPA de otro módulo.
3. **Inmutabilidad y DTOs:**
   - Emplea Java `record` para todos los DTOs de entrada y salida (`Request` y `Response`).
4. **Mapeo Explícito:**
   - Usa mappers dedicados (`*PersistenceMapper`) para traducir entre entidades JPA y modelos de dominio, evitando que los detalles de la base de datos contaminen la lógica de negocio.
