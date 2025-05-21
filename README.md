---

# 🧹 Marketplace de Servicios Domésticos

## 📝 Descripción General

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
    Page<Servicio> findByCategoria(String categoria, Pageable page);
    @Query("SELECT s FROM Servicio s WHERE distance(s.ubicacion, :coord) < :rango")
    Page<Servicio> findPorProximidad(@Param("coord") Point coord,
                                     @Param("rango") double rango,
                                     Pageable page);
    Page<Servicio> findByTarifaBetween(double min, double max, Pageable page);
    Page<Servicio> findByRatingGreaterThanEqual(double rating, Pageable page);
}
public interface HorarioRepository extends JpaRepository<Horario, Long> {
    List<Horario> findByServicioIdAndDiaSemana(Long servicioId, DiaSemana dia);
}
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findByClienteId(Long clienteId);
    List<Reserva> findByProveedorIdAndEstado(Long proveedorId, EstadoReserva estado);
}
public interface PagoRepository extends JpaRepository<Pago, Long> {}
public interface ReseñaRepository extends JpaRepository<Reseña, Long> {
    List<Reseña> findByServicioId(Long servicioId);
}
```

### Capa de Servicios (lógica de negocio)

#### ClienteService

```java
ClienteDto registrar(ClienteRequestDto dto);
TokenDto login(LoginDto dto);
Page<ServicioDto> buscarServicios(FiltroServicioDto filtros, Pageable page);
ReservaDto crearReserva(ReservaRequestDto dto);
void cancelarReserva(Long reservaId, Long clienteId);
List<ReservaDto> misReservas(Long clienteId);
```

#### ProveedorService

```java
ProveedorDto registrar(ProveedorRequestDto dto);
ServicioDto crearServicio(ServicioRequestDto dto);
ServicioDto actualizarServicio(Long id, ServicioRequestDto dto);
void definirDisponibilidad(Long servicioId, List<HorarioDto> horarios);
List<ReservaDto> verReservasPendientes(Long proveedorId);
void aceptarReserva(Long reservaId);
void completarReserva(Long reservaId);
```

#### ReservaService

```java
Page<ReservaDto> obtenerReservasPorFiltro(ReservaFiltroDto filtros, Pageable page);
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
### Capa de controladores REST API
#### ClienteController

| Método | Ruta                                         | Parámetros                                                                       | Tipo Parámetros                           | Código HTTP | Descripción                                                                                   |
| ------ | -------------------------------------------- | -------------------------------------------------------------------------------- | ----------------------------------------- | ----------- | --------------------------------------------------------------------------------------------- |
| POST   | /api/clientes                                | request body                                                                     | ClienteRequestDto (DTO)                   | 201         | Crea un nuevo perfil de cliente y envía confirmación de registro.                             |
| POST   | /api/clientes/login                          | request body                                                                     | LoginDto (DTO)                            | 200         | Valida credenciales y devuelve un token JWT para acceso a recursos protegidos.                |
| GET    | /api/servicios                               | query parameters (filtros de categoría, ubicación, precio, calificación, paging) | FiltroServicioDto (DTO)                   | 200         | Recupera lista paginada de servicios aplicando filtros según criterios proporcionados.        |
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
