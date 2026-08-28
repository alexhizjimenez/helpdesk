# 🎫 Helpdesk API - Arquitectura Hexagonal Modular

Backend del sistema **Helpdesk** desarrollado con **Java 21** y **Spring Boot 4.1.0**, diseñado bajo los principios de **Arquitectura Hexagonal (Puertos y Adaptadores)** y estructurado por características o subdominios (**Package-by-Feature / Monolito Modular**), con persistencia en **PostgreSQL** y capa de caché distribuida con **Redis**.

---

## 📌 Tabla de Contenidos
- [Descripción del Proyecto](#-descripción-del-proyecto)
- [Arquitectura y Principios de Diseño](#-arquitectura-y-principios-de-diseño)
- [Estructura del Proyecto](#-estructura-del-proyecto)
- [Módulos del Sistema](#-módulos-del-sistema)
- [Catálogo de Endpoints (REST API)](#-catálogo-de-endpoints-rest-api)
- [Estrategia de Caché con Redis](#-estrategia-de-caché-con-redis)
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
- Caché de alto rendimiento para consultas recurrentes.

---

## 🏛️ Arquitectura y Principios de Diseño

El proyecto sigue una **Arquitectura Hexagonal Modular (*Package-by-Feature*)**, donde cada módulo de negocio representa un subdominio autocontenido con su propio hexágono independiente:

```text
                        +----------------------------------------------------+
                        |            INFRASTRUCTURE (Adaptadores)            |
                        |  +----------------------------------------------+  |
                        |  |         APPLICATION (Casos de uso)           |  |
                        |  |  +----------------------------------------+  |  |
                        |  |  |          DOMAIN (Modelo puro)          |  |  |
[REST Controller (IN)] ---> [Inbound Port] --> [Service] <---> [Domain]|  |
                        |  |                     |  |                  |  |  |
                        |  |  +-[Cache Aside]----+  +--[Outbound Port]-+  |  |
                        |  +----|------------------------------|----------+  |
                        |       v                              v             |
                        |  [Redis Cache]         [Persistence Adapter / JPA] |
                        +--------------------------------------|-------------+
                                                               v
                                                         [PostgreSQL]
```

### Capas dentro de cada módulo:

1. **`domain` (Núcleo del Dominio):**
   - **`model/`**: Entidades y modelos de negocio puros en Java estándar, libres de dependencias de frameworks o anotaciones JPA.
   - **`exception/`**: Excepciones específicas del subdominio (ej. `DepartmentAlreadyExistsException`, `RoleNotFoundException`).

2. **`application` (Lógica de Aplicación y Casos de Uso):**
   - **`port/in/`**: Interfaces que definen los casos de uso expuestos a los actores externos (ej. `CreateDepartmentUseCase`, `ListDepartmentsUseCase`).
   - **`port/out/`**: Puertos de salida que definen las necesidades secundarias como persistencia (ej. `DepartmentRepositoryPort`, `UserRepositoryPort`).
   - **`service/`**: Implementación de los casos de uso, orquestación de lógica, reglas de negocio y manejo de caché.

3. **`infrastructure` (Adaptadores e Integraciones Técnicas):**
   - **`adapter/in/rest/`**: Controladores Spring MVC REST (`@RestController`) y DTOs inmutables de solicitud/respuesta (`dto/` usando Java `record`).
   - **`adapter/out/persistence/`**: Entidades JPA (`*Entity`), repositorios Spring Data JPA (`*Repository`), mappers (`*PersistenceMapper`) y adaptadores que implementan los puertos de salida (`*PersistenceAdapter`).

4. **`shared` (Componentes Transversales):**
   - Excepciones base reutilizables (`DomainException`, `BusinessException`, `ResourceNotFoundException`, `ResourceAlreadyExistsException`).
   - Manejador global de excepciones REST (`GlobalExceptionHandler`) y estructura estándar de error (`ErrorResponse`).
   - Configuración centralizada de caché e infraestructura (`RedisConfig`).

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
│       ├── adapter/in/rest/
│       │   ├── GlobalExceptionHandler.java # Captura y unificación global de errores
│       │   └── dto/
│       │       └── ErrorResponse.java   # Esquema de respuesta ante fallos
│       └── config/
│           └── RedisConfig.java         # Configuración de RedisTemplate y Serializers
│
├── department/                          # Subdominio de Departamentos (CRUD + Redis)
│   ├── domain/
│   │   ├── model/Department.java
│   │   └── exception/
│   ├── application/
│   │   ├── port/in/                     # Use Cases (Create, List, GetById, Update, Delete)
│   │   ├── port/out/                    # DepartmentRepositoryPort
│   │   └── service/                     # DepartmentService (Cache-Aside + Eviction)
│   └── infrastructure/
│       └── adapter/
│           ├── in/rest/                 # DepartmentController & DTOs (Request/Response)
│           └── out/persistence/         # Entity, JPA Repository, Adapter & Mapper
│
├── role/                                # Subdominio de Roles
│   ├── domain/model/Role.java
│   ├── application/
│   │   ├── port/in/CreateRoleUseCase.java
│   │   ├── port/out/RoleRepositoryPort.java
│   │   └── service/RoleService.java
│   └── infrastructure/
│       └── adapter/
│           ├── in/rest/RoleController.java
│           └── out/persistence/
│
├── user/                                # Subdominio de Usuarios (Integración Inter-Módulos)
│   ├── domain/model/User.java
│   ├── application/
│   │   ├── port/in/CreateUserUseCase.java
│   │   ├── port/out/UserRepositoryPort.java
│   │   └── service/UserService.java
│   └── infrastructure/
│       └── adapter/
│           ├── in/rest/UserController.java
│           └── out/persistence/
│
├── ticket/                              # Subdominio de Tickets (Mesa de ayuda)
├── category/                            # Subdominio de Categorías
├── sla/                                 # Subdominio de Acuerdos de Nivel de Servicio
├── notification/                        # Subdominio de Notificaciones
└── auth/                                # Subdominio de Autenticación y Seguridad
```

---

## 📦 Módulos del Sistema

| Módulo | Estado | Descripción |
|---|:---:|---|
| **`shared`** | 🟢 Activo | Excepciones base, manejador global de errores (`@RestControllerAdvice`) y configuración de Redis. |
| **`department`** | 🟢 Completo | CRUD total (Crear, Listar, Obtener por ID, Actualizar, Eliminar) con patrón **Cache-Aside** en Redis. |
| **`role`** | 🟢 Activo | Gestión y persistencia de roles del sistema. |
| **`user`** | 🟢 Activo | Gestión de usuarios con validación cruzada y resolución de departamentos y roles vía puertos de salida. |
| **`ticket`** | 🔨 Estructurado | Creación, seguimiento, transiciones de estado, prioridad y resolución de tickets. |
| **`category`** | 🔨 Estructurado | Clasificación y categorización de requerimientos. |
| **`sla`** | 🔨 Estructurado | Reglas de tiempos máximos para primera respuesta y resolución. |
| **`notification`** | 🔨 Estructurado | Mecanismo de envío de alertas y notificaciones por eventos. |
| **`auth`** | 🔨 Estructurado | Autenticación, generación y validación de tokens JWT y permisos. |

---

## 🌐 Catálogo de Endpoints (REST API)

### 🏢 Departamentos (`/api/departments`)

| Método | Endpoint | Descripción | Código Éxito |
|---|---|---|:---:|
| `POST` | `/api/departments` | Crea un nuevo departamento | `201 Created` |
| `GET` | `/api/departments` | Lista departamentos (con caché Redis) | `200 OK` |
| `GET` | `/api/departments/{id}` | Obtiene un departamento por ID | `200 OK` |
| `PUT` | `/api/departments/{id}` | Actualiza un departamento e invalida caché | `202 Accepted` |
| `DELETE` | `/api/departments/{id}` | Elimina un departamento e invalida caché | `204 No Content` |

#### Ejemplo Payload: Crear / Actualizar Departamento
```json
{
  "name": "Tecnología de la Información",
  "active": true
}
```

---

### 🛡️ Roles (`/api/roles`)

| Método | Endpoint | Descripción | Código Éxito |
|---|---|---|:---:|
| `POST` | `/api/roles/` | Crea un nuevo rol | `201 Created` |

#### Ejemplo Payload: Crear Rol
```json
{
  "name": "ROLE_ADMIN"
}
```

---

### 👤 Usuarios (`/api/users`)

| Método | Endpoint | Descripción | Código Éxito |
|---|---|---|:---:|
| `POST` | `/api/users/` | Crea un usuario asignando departamento y roles | `201 Created` |

#### Ejemplo Payload: Crear Usuario
```json
{
  "fullName": "Alex Jimenez",
  "email": "alex@example.com",
  "password": "SecurePassword123!",
  "phone": "+521234567890",
  "departmentId": "a3b8c4d2-1111-4444-9999-000000000001",
  "roleIds": [
    "b5c9d6e3-2222-5555-aaaa-000000000002"
  ]
}
```

---

## ⚡ Estrategia de Caché con Redis

El módulo de departamentos implementa el patrón **Cache-Aside** con serialización JSON polimórfica:

1. **Lectura (`GET /api/departments`):**
   - **Cache Hit:** Si la clave `departments` existe en Redis, se retornan los datos directamente en milisegundos sin tocar PostgreSQL.
   - **Cache Miss:** Si no existe, consulta la base de datos PostgreSQL, almacena la lista en Redis con un **TTL de 10 minutos**, y retorna la respuesta.
   - **Resiliencia / Fallback:** Si el servidor Redis no está disponible o presenta problemas de conexión, la aplicación captura la excepción en log y consulta la base de datos de manera transparente sin interrumpir el servicio.
2. **Invalidación (`Eviction`):**
   - Al ejecutar `create()`, `updateDepartment()` o `deleteDepartmentById()`, se invoca `evictDepartmentsCache()` para purgar la clave `departments` de Redis y garantizar que las lecturas posteriores no sirvan datos desactualizados.

---

## 🛠️ Stack Tecnológico

- **Lenguaje:** Java 21 (LTS)
- **Framework Principal:** Spring Boot 4.1.0
  - Spring MVC (Web RESTful APIs)
  - Spring Data JPA
  - Spring Data Redis
  - Spring Validation (Jakarta Validation)
- **Bases de Datos & Caché:**
  - **PostgreSQL** (Almacenamiento relacional principal)
  - **Redis** (Caché en memoria distribuida)
- **Herramientas & Librerías:** Lombok, Jackson (Tools Jackson 3), DevTools
- **Testing:** JUnit 5, AssertJ, Spring Boot Starter Test
- **Build Tool:** Apache Maven (vía `mvnw` wrapper)

---

## 🚀 Configuración y Ejecución

### Prerrequisitos
- **JDK 21** instalado y configurado (`java -version`).
- **PostgreSQL** en ejecución en el puerto `5432`.
- **Redis** en ejecución en el puerto `6379`.

> [!TIP]
> Puedes levantar PostgreSQL y Redis rápidamente con Docker:
> ```bash
> # PostgreSQL
> docker run -d --name helpdesk-postgres -e POSTGRES_DB=helpdesk -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=root -p 5432:5432 postgres:16-alpine
> 
> # Redis
> docker run -d --name helpdesk-redis -p 6379:6379 redis:7-alpine
> ```

### 1. Configuración de la Aplicación
Las propiedades se configuran en `src/main/resources/application.yml`:

```yaml
spring:
  application:
    name: helpdesk
  datasource:
    url: jdbc:postgresql://localhost:5432/helpdesk
    username: postgres
    password: root
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.PostgreSQLDialect
  data:
    redis:
      host: localhost
      port: 6379
      password: ${REDIS_PASSWORD:}
      timeout: 2000
  cache:
    type: redis
    redis:
      time-to-live: 60000

server:
  port: 8080
```

### 2. Compilar y Ejecutar

```bash
# Compilar el proyecto y verificar pruebas
./mvnw clean compile test

# Ejecutar la aplicación Spring Boot
./mvnw spring-boot:run
```

---

## 📐 Buenas Prácticas para Nuevos Módulos

1. **Aislamiento del Dominio:**
   - Nunca agregues anotaciones de frameworks (`@Entity`, `@Table`, `@RestController`, `@Autowired`) dentro de `domain/model`.
2. **Comunicación entre Módulos:**
   - Si un módulo necesita comunicarse con otro (por ejemplo, `user` consultando `department`), debe hacerlo mediante interfaces de puertos (`port/in` o `port/out`), nunca accediendo directamente a las entidades JPA ni a los repositorios de otro módulo.
3. **Inmutabilidad en DTOs:**
   - Emplea Java `record` para todos los DTOs de entrada y salida (`Request` y `Response`).
4. **Mapeo Explícito:**
   - Emplea mappers dedicados (`*PersistenceMapper`) para traducir entre entidades JPA y modelos de dominio, desacoplando los detalles del esquema de la base de datos de la lógica de negocio.
