# 🧹 Marketplace de Servicios Domésticos
## CS 2031 – Desarrollo Basado en Plataforma  
### Integrantes del equipo
- **Rafael Oliverth Solier Soto**  
- **Yulinio Zavala Mariño**  
---
## Índice
1. [Introducción](#introducción)
2. [Identificación del Problema o Necesidad](#identificación-del-problema-o-necesidad)
3. [Descripción de la Solución](#descripción-de-la-solución)  
   3.1. [Funcionalidades Implementadas](#funcionalidades-implementadas)  
   3.2. [Tecnologías Utilizadas](#tecnologías-utilizadas)
4. [Modelo de Entidades](#modelo-de-entidades)
5. [Testing y Manejo de Errores](#testing-y-manejo-de-errores)
6. [Medidas de Seguridad Implementadas](#medidas-de-seguridad-implementadas)
7. [Eventos y Asincronía](#eventos-y-asincronía)
8. [Uso de GitHub](#uso-de-github)
9. [Conclusión](#conclusión)
10. [Apéndices](#apéndices)
## 📝 Descripción General
---

## Introducción

**Contexto: ✏️**  
En las zonas urbanas de Perú, encontrar rápidamente profesionales confiables para tareas domésticas —desde una simple limpieza profunda hasta reparaciones eléctricas de urgencia— continúa siendo un dolor de cabeza. La oferta de servicios suele estar dispersa en redes sociales, anuncios impresos o recomendaciones informales, lo que dificulta comparar precios, disponibilidad y reputación. Además, la informalidad del sector limita la transparencia y la seguridad tanto para clientes como para proveedores.

**Objetivos del proyecto: 🎯**
- Facilitar el **registro y acceso seguro** de clientes y proveedores.
- Ofrecer a los proveedores una **vitrina digital** para publicar y administrar sus servicios domésticos.
- Permitir a los clientes **descubrir, reservar y pagar** dichos servicios de forma sencilla.
- Fomentar la **confianza** mediante un sistema transparente de reseñas y calificaciones tras cada servicio.

---
## Identificación del Problema o Necesidad

**Descripción del problema: 🤔**  
La informalidad y falta de estandarización en el mercado de servicios domésticos provoca frustración en los hogares: citaciones que nunca llegan, trabajos de mala calidad y sobrecostos de último minuto. Simultáneamente, miles de técnicos y especialistas capacitados carecen de un canal digital que les permita visibilizar su trabajo y escalar su negocio de manera formal.

**Justificación: ✅**  
Resolver esta brecha es clave para mejorar la calidad de vida en los hogares urbanos y formalizar la economía de los trabajadores del sector. Al conectar clientes y proveedores en un entorno regulado, con procesos de verificación y reputación, el proyecto reduce asimetrías de información, fomenta el empleo formal y contribuye a la profesionalización del servicio doméstico en el país.

---
## Descripción de la Solución
La aplicación seguirá arquitectura hexagonal:
```text 
Controller → Service → Entities → Database
        ↕          ↕       ↕
     DTO/Mapper    Repository
```
### Funcionalidades Implementadas
Tenemos que explicar
### Tecnologías Utilizadas
- **Java 17 + Spring Boot 3.3** → núcleo del backend y arranque rápido.
- **Spring Starters** (Web, Data JPA, Security, Validation, AOP) → HTTP, persistencia, seguridad y validación listos “out-of-the-box”.
- **PostgreSQL 15** (producción) + **HikariCP** → base de datos robusta con *pool* de conexiones veloz.
- **H2 en memoria** + **Testcontainers** → pruebas locales e integración sin depender de una DB externa.
- **JWT** + **BCrypt 12** → autenticación *stateless* y contraseñas seguras.
- **Lombok** → elimina getters/setters y reduce código repetitivo.
- **Maven** → compila, prueba y empaqueta con un solo comando.
- **JUnit 5 + Spring Boot Test + Mockito** → pruebas unitarias e integración automatizadas.
- **GitHub Actions** → ejecuta build, tests y crea la imagen Docker en cada *push*.
- **Docker 20.10** → despliegue idéntico en cualquier servidor.
---
## Modelo de Entidades
falta realizar
## Testing y Manejo de Errores
#### Pirámide de testing

| Capa | Clases de prueba | Framework | Propósito |
|------|-----------------|-----------|-----------|
| **Unitarias – Dominio** | 8 | JUnit 5 | Valida reglas de entidades (`equals`, `hashCode`, restricciones de negocio). |
| **Integración – Controladores** | 6 | `@SpringBootTest` + **MockMvc** | Ejecuta endpoints REST con contexto Spring completo y base H2 embebida. |
| **Integración – Repositorios** | 3 | `@SpringBootTest` + **Testcontainers** | Verifica consultas JPA sobre un PostgreSQL 15 efímero. |
| **Smoke** | 1 | `@SpringBootTest` | Comprueba que el contexto de la aplicación levanta correctamente. |

> **Totales:** 18 clases de prueba ≈ 270 aserciones ·
#### Manejo de errores
El paquete `exception` centraliza y normaliza todas las fallas de la aplicación.

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiError> handleValidationErrors() { }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ApiError> handleNotFound() { }

  // Otros mapeos: ConflictException, JwtException, etc.
}
```

| Escenario      | Componente Spring               | HTTP | Payload de Respuesta                          |
|---------------|---------------------------------|------|-----------------------------------------------|
| **Validación DTO** | `MethodArgumentNotValidException` | 400  | `{ timestamp, status, errors[{field, message}], path }` |
| **Recurso inexistente** | `ResourceNotFoundException`     | 404  | `{ timestamp, status, error, message, path }` |
| **Conflicto de negocio** | `ConflictException`             | 409  | `{ timestamp, status, error, message, path }` |
| **Token ausente/expirado** | `RestAuthenticationEntryPoint`  | 401  | `{ timestamp, status, error: "Unauthorized", message, path }` |
| **Permiso insuficiente** | `RestAccessDeniedHandler`       | 403  | `{ timestamp, status, error: "Forbidden", message, path }` |
| **Errores JWT** | `JwtException`                  | 401  | `Motivo del fallo (firma, expiración, etc.)`  |
| **Internal Server Error**          | `Exception`                     | 500  | `Error inesperado`                                           |
    

## Medidas de Seguridad Implementadas
La seguridad en las aplicaciones es fundamental para proteger los datos sensibles, garantizar la integridad del sistema y prevenir accesos no autorizados. Las siguientes medidas han sido implementadas para ofrecer una arquitectura robusta y alineada con buenas prácticas de desarrollo seguro.
| **Capa**            | **Medida**               | **Detalle**                                                                 |
|---------------------|---------------------------|------------------------------------------------------------------------------|
| **Autenticación**   | JWT                       | Tokens HS512, exp. 60 min; endpoint de _refresh_.                           |
| **Almacenamiento**  | BCrypt 12                 | _Hash_ y _salt_ de contraseñas.                                             |
| **Autorización**    | Spring Method Security    | `@PreAuthorize` para proteger recursos.                                     |
| **Datos en tránsito** | HTTPS                   | Configurado en proxy Nginx.                                                 |
| **Validación**      | Bean Validation           | `@NotBlank`, `@Email`; respuestas 400 uniformes.                            |
| **Prevención**      | JPQL parametrizado, CORS  | CORS restrictivo, filtro XSS básico.                                        |

## Eventos y Asincronía
En **Marketplace**, los eventos y la asincronía juegan un papel importante para mejorar la eficiencia del sistema, especialmente en tareas que no requieren una respuesta inmediata. El envío de correos electrónicos es uno de los principales ejemplos de este enfoque. En lugar de procesar estas tareas de manera síncrona, lo cual podría generar demoras innecesarias para el usuario, se ejecutan en segundo plano, permitiendo que la experiencia sea más fluida.
#### Casos de uso del envío de correos electrónicos:
1. Registro de un nuevo Cliente/proveedor:
- Cuando un nuevo usuario se registra en la plataforma, se dispara un evento que envía de forma asíncrona un correo electrónico de bienvenida. Este correo confirma el registro del Cliente/Proveedor y proporciona información útil para comenzar a interactuar en la plataforma. El envío de este correo en segundo plano permite que el usuario complete el proceso de registro sin esperas innecesarias.
2. Rafita:

## Uso de GitHub
El desarrollo de **Marketplace** se gestionó de forma colaborativa utilizando **GitHub**, empleando un flujo de trabajo basado en ramas, issues y pull requests para organizar y revisar el trabajo de todo el equipo.

#### 🗂️Ramas (Branches)
Cada nueva funcionalidad o corrección de errores se desarrolló en **ramas independientes** para evitar conflictos con la rama principal (`main`). Esto permitió que los miembros del equipo trabajaran en paralelo de manera eficiente y ordenada.

#### 📌 Issues
Se utilizaron **issues** para:

- Asignar tareas
- Reportar errores
- Gestionar el progreso del proyecto
#### 🔄 Pull Requests
Antes de fusionar cualquier cambio en la rama principal, se creaba un **pull request (PR)**.  Esto permitió que los cambios fueran revisados y discutidos por el equipo, asegurando la **calidad del código** antes de su integración.

#### ✅ Beneficios del Flujo de Trabajo

Este flujo de trabajo colaborativo permitió:

- Mantener el proyecto **organizado**
- Mejorar la calidad del código mediante **revisiones**
- Asegurar una **integración continua sin problemas**
---
## Conclusión
### Logros del Proyecto: 📝
El desarrollo de **Marketplace de Servicios Domésticos** ha materializado una plataforma que conecta de forma segura y transparente a hogares con técnicos y especialistas confiables. Se integraron módulos completos de autenticación JWT, catálogo y reserva de servicios, pagos simulados y un sistema de reputación que reduce la informalidad del sector. Gracias a ello, los clientes pueden contratar mano de obra calificada con unos pocos clics, mientras que los proveedores obtienen visibilidad y un canal formal de ingresos. El despliegue automatizado vía GitHub Actions y Docker garantiza que la aplicación sea repetible y fácil de escalar.

### Aprendizajes Clave: 📚
- **Diseño Hexagonal:** Separar dominio, infraestructura y puertos REST nos facilitó las pruebas y dio flexibilidad para cambios futuros.
- **Seguridad End-to-End:** Implementar Spring Security con JWT y BCrypt nos enseñó buenas prácticas de cifrado y manejo de sesiones _stateless_.
- **CI/CD en la nube:** Automatizar compilación, pruebas y empaquetado Docker en cada _push_ nos demostró la importancia de la entrega continua para mantener la calidad.
- **Trabajo Colaborativo en GitHub Projects:** La gestión de issues y _pull requests_ con revisiones cruzadas fomentó feedback constante y detección temprana de bugs.

### Trabajo Futuro: 🚀
- **Pagos en tiempo real:** Integrar una pasarela como Culqi o Mercado Pago para transacciones reales.
- **Geolocalización y horarios en vivo:** Mostrar proveedores cercanos y disponibilidad en tiempo real mediante sockets.
- **Chat cliente-proveedor:** Habilitar mensajería directa y envío de fotos antes de la visita.
- **Facturación electrónica SUNAT:** Generar comprobantes válidos para proveedores formalizados.
- **Monetización y fidelización:** Implementar planes premium para proveedores y un sistema de puntos o descuentos para clientes recurrentes.
---
## Apéndices
**Licencia:** MIT  
**Referencias:** Spring Boot Reference, apuntes del curso


De aquí para abajo es el contenido del README.md antiguo, que tiene algunas cosas que se usarán.


El proyecto tiene como objetivo desarrollar una aplicación web y móvil tipo marketplace que conecte a usuarios con proveedores de servicios domésticos como plomería, electricidad, limpieza, entre otros. El sistema permitirá a los clientes buscar servicios por categoría y ubicación, realizar reservas en tiempo real, gestionar pagos y dejar reseñas, mientras que los proveedores podrán administrar sus servicios, horarios y visualizar su historial laboral.

---

2. Tecnologías y Arquitectura

Backend: Java 17+, Spring Boot 3.x, Spring Security (JWT), Spring Data JPA.

Base de Datos: PostgreSQL.

ORM: JPA/Hibernate.

Frontend: React (opcional), integrado vía API REST.

Servicios Externos: Stripe

Testing: JUnit, Mockito, Spring Test.

La aplicación seguirá arquitectura hexagonal:
```text 
Controller → Service → Entities → Database
        ↕          ↕       ↕
     DTO/Mapper    Repository
```
---

## Modelo de Datos y Relaciones

### Entidades Principales

| Entidad   | Atributos clave                                                    | Relaciones                          |
| --------- | ------------------------------------------------------------------ | ----------------------------------- |
| Cliente   | id, nombre, apellido, email, teléfono, contraseña(encrypted), foto | 1\:N Reservas, 1\:N Reseñas         |
| Proveedor | id, nombre, apellido, email, teléfono, contraseña, foto, rating    | 1\:N Servicios, 1\:N Reservas       |
| Servicio  | id, nombre, descripción, tarifa, categoría (como un enum: LIMPIEZA, PLOMERIA, ELECTRICISTA, CARPINTERIA, PINTURA, JARDINERIA, CUIDADOS) | N:1 Proveedor, 1\:N Horarios        |
| Disponibilidad   | id, díaSemana, horaInicio, horaFin                           | N:1 Servicio                        |
| Reserva   | id, fechaReservada, dirección, estado (como un emun: GENERADO, PAGADO, ACEPTADO, CANCELADO, TERMINADO) | N:1 Cliente, N:1 Servicio, 1:1 Pago |
| Pago      | id, monto, fecha, estado                                     | 1:1 Reserva                         |
| Reseña    | id, puntuación(1-5), comentario, fecha                             | N:1 Cliente, N:1 Servicio           |

---

## Capas y Métodos Clave

### Capa de Repositorios (Spring Data JPA)

Definir una interfaz por entidad, adaptando consultas a filtros de categoría, proximidad, precio y calificación:

```java
public interface ClienteRepository extends JpaRepository<Cliente, Long> {}
public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {}
public interface ServicioRepository extends JpaRepository<Servicio, Long> {
    Servicio findByCategoria(String categoria);
    List<Servicio> findByTarifaBetween(double min, double max);
    List<Servicio> findByRatingGreaterThanEqual(double rating);
}
public interface DisponibilidadRepository extends JpaRepository<Horario, Long> {
    List<Disponibilidad> findByServicioIdAndDiaSemana(Long servicioId, DiaSemana dia);
}
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findByClienteId(Long clienteId);
    List<Reserva> findByProveedorIdAndEstado(Long proveedorId, EstadoReserva estado);
}
public interface PagoRepository extends JpaRepository<Pago, Long> {}
public interface ResenaRepository extends JpaRepository<Resena, Long> {
    List<Resena> findByServicioId(Long servicioId);
}
```

### Capa de Servicios (lógica de negocio)

#### ClienteService

```java
ClienteDto registrar(ClienteRequestDto dto);
TokenDto login(LoginDto dto);
List<ServicioDto> buscarServicios(FiltroServicioDto filtros);
ReservaDto crearReserva(ReservaRequestDto dto);
void cancelarReserva(Long reservaId, Long clienteId);
List<ReservaDto> misReservas(Long clienteId);
```

#### ProveedorService

```java
ProveedorDto registrar(ProveedorRequestDto dto);
ServicioDto crearServicio(ServicioRequestDto dto);
ServicioDto actualizarServicio(Long id, ServicioRequestDto dto);
void definirDisponibilidad(Long servicioId, List<DisponibilidadDto> Disponibilidad);
List<ReservaDto> verReservasPendientes(Long proveedorId);
void aceptarReserva(Long reservaId);
void completarReserva(Long reservaId);
```

#### ReservaService

```java
List<ReservaDto> obtenerReservasPorFiltro(ReservaFiltroDto filtros);
ReservaDto detalleReserva(Long reservaId);
```

#### PagoService

```java
PagoDto procesarPago(Long reservaId, PagoRequestDto dto);
```

#### ReseñaService

```java
ReseñaDto crearReseña(ReseñaRequestDto dto);
List<ReseñaDto> listarReseñas(Long servicioId);
```

### DisponibilidadService

```java
List<Disponibilidad> obtenerDisponibilidadByServicioId(Long servicio_id);
Disponibilidad crearDisponibilidad(DisponibilidadDto dto);
```

### Capa de controladores REST API
#### ClienteController

| Método | Ruta                                         | Parámetros                                                                       | Tipo Parámetros                           | Código HTTP | Descripción                                                                                   |
| ------ | -------------------------------------------- | -------------------------------------------------------------------------------- | ----------------------------------------- | ----------- | --------------------------------------------------------------------------------------------- |
| POST   | /api/clientes                                | request body                                                                     | ClienteRequestDto (DTO)                   | 201         | Crea un nuevo perfil de cliente y envía confirmación de registro.                             |
| POST   | /api/clientes/login                          | request body                                                                     | LoginDto (DTO)                            | 200         | Valida credenciales y devuelve un token JWT para acceso a recursos protegidos.                |
| GET    | /api/servicios                               | query parameters (filtros de categoría, direccion, precio, calificación, paging) | FiltroServicioDto (DTO)                   | 200         | Recupera lista paginada de servicios aplicando filtros según criterios proporcionados.        |
| POST   | /api/clientes/{id}/reservas                  | path parameter: id, request body                                                 | Long (Entity id), ReservaRequestDto (DTO) | 201         | Registra una nueva reserva para el cliente, vinculándola al servicio y horario seleccionados. |
| PATCH  | /api/clientes/{id}/reservas/{resId}/cancelar | path parameters: id, resId                                                       | Long (Entity ids)                         | 204         | Cambia el estado de la reserva a 'CANCELADA' si aún está en estado pendiente.                 |
| GET    | /api/clientes/{id}/reservas                  | path parameter: id                                                               | Long (Entity id)                          | 200         | Devuelve todas las reservas asociadas al cliente, con su estado y detalles.                   |

#### ProveedorController

| Método | Ruta                            | Parámetros                       | Tipo Parámetros                            | Código HTTP | Descripción                                                                              |
| ------ | ------------------------------- | -------------------------------- | ------------------------------------------ | ----------- | ---------------------------------------------------------------------------------------- |
| POST   | /api/proveedores                | request body                     | ProveedorRequestDto (DTO)                  | 201         | Crea un nuevo perfil de proveedor, registrando datos y perfil de servicios.              |
| POST   | /api/proveedores/{id}/servicios | path parameter: id, request body | Long (Entity id), ServicioRequestDto (DTO) | 201         | Agrega un nuevo servicio al catálogo del proveedor con detalles como tarifa y categoría. |
| PUT    | /api/servicios/{id}             | path parameter: id, request body | Long (Entity id), ServicioRequestDto (DTO) | 200         | Modifica datos de un servicio existente (tarifa, descripción, categoría).                |
| POST   | /api/servicios/{id}/horarios    | path parameter: id, request body | Long (Entity id), List<HorarioDto> (DTO)   | 204         | Establece horarios disponibles para el servicio, especificando días y franjas horarias.  |
| GET    | /api/proveedores/{id}/reservas  | path parameter: id               | Long (Entity id)                           | 200         | Muestra reservas en estados 'PENDIENTE' o 'ACEPTADA' asignadas al proveedor.             |
| PATCH  | /api/reservas/{resId}/aceptar   | path parameter: resId            | Long (Entity id)                           | 200         | Cambia el estado de la reserva a 'ACEPTADA', bloqueando el horario correspondiente.      |
| PATCH  | /api/reservas/{resId}/completar | path parameter: resId            | Long (Entity id)                           | 200         | Marca la reserva como 'COMPLETADA' una vez finalizado el servicio.                       |

#### PagoController

| Método | Ruta                   | Parámetros                              | Tipo Parámetros                        | Código HTTP | Descripción                                                        |
| ------ | ---------------------- | --------------------------------------- | -------------------------------------- | ----------- | ------------------------------------------------------------------ |
| POST   | /api/pagos/{reservaId} | path parameter: reservaId, request body | Long (Entity id), PagoRequestDto (DTO) | 201         | Registra y procesa el pago asociado a una reserva mediante Stripe. |

#### ReseñaController

| Método | Ruta                        | Parámetros         | Tipo Parámetros        | Código HTTP | Descripción                                                                         |
| ------ | --------------------------- | ------------------ | ---------------------- | ----------- | ----------------------------------------------------------------------------------- |
| POST   | /api/resenas                | request body       | ReseñaRequestDto (DTO) | 201         | Permite al cliente añadir una calificación y comentario tras completar un servicio. |
| GET    | /api/servicios/{id}/resenas | path parameter: id | Long (Entity id)       | 200         | Recupera todas las reseñas y puntuaciones asociadas a un servicio.                  |

#### ReservaController

| Método | Ruta                        | Parámetros         | Tipo Parámetros        | Código HTTP | Descripción                                                        |
|--------|-----------------------------| ------------------ |------------------------|-------------|--------------------------------------------------------------------|
| GET    | /api/reservas               | request body       | List<ReservaDto> (DTO) | 200         | Permite ver todas la reservas                                      |

---

## 📦 DTOs y Validaciones

Ejemplo de validaciones comunes:

* `nombre`: no nulo, 2-50 caracteres
* `correo`: formato válido
* `contraseña`: 6-50 caracteres, al menos un número y símbolo
* `monto`: > 0

Se recomienda el uso de:

* `@Valid`
* `@NotNull`, `@Email`, `@Size`, `@Min`, etc.

---

## ❌ Manejo de Errores

* **400 Bad Request**: Fallos de validación (Bean Validation).
* **401 Unauthorized**: Token JWT inválido o expirado.
* **403 Forbidden**: Permiso denegado.
* **404 Not Found**: Entidad no encontrada.
* **409 Conflict**: Reserva o recurso duplicado.
* **500 Internal Server Error**: Error inesperado.
Controlados globalmente con `@ControllerAdvice`

---

## 🔔 Eventos Asíncronos
* Usar eventos de Spring para disparar correos electrónicos en:

  * Confirmación de registro.
  * Confirmación de pago
  * Creación, cancelación y finalización de reservas.
* **Webhooks** de Stripe para actualizar el estado de pagos.


---

---
## Integraciones de Terceros

* **Stripe**: Procesamiento de pagos.
* **Spring Mail** (JavaMailSender): Envío de notificaciones por correo.

---

## 🚀 Despliegue

* Base de datos PostgreSQL en un contenedor Docker local. Configurar el aplication.properties como mínimo:
```
spring.application.name=eventosConWhereby

spring.datasource.url=jdbc:postgresql://localhost:5555/postgres
spring.datasource.username=postgres
spring.datasource.password=123
spring.jpa.hibernate.ddl-auto=update

spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=${MAIL_USERNAME}
spring.mail.password=${MAIL_PASSWORD}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```
* Despliegue sugerido en Node.js con React (frontend)

---
