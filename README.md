---

# 🧹 Marketplace de Servicios Domésticos

## 📝 Descripción General

El proyecto tiene como objetivo desarrollar una aplicación web y móvil tipo marketplace que conecte a usuarios con proveedores de servicios domésticos como plomería, electricidad, limpieza, entre otros. El sistema permitirá a los clientes buscar servicios por categoría y ubicación, realizar reservas en tiempo real, gestionar pagos y dejar reseñas, mientras que los proveedores podrán administrar sus servicios, horarios y visualizar su historial laboral.

---

## ⚙️ Tecnologías y Herramientas

* **Java 17+**
* **Spring Boot 3.x**
* **Spring Data JPA**
* **Spring Security + JWT**
* **PostgreSQL**
* **Stripe API / PayPal (modo sandbox)**
* **SendGrid / Twilio / Google Maps API**
* **JUnit / Mockito / Testcontainers**

---

## 🧩 Entidades Principales

### 🧍 Usuario

Los usuarios pueden ser **Clientes** (contratan servicios) o **Proveedores** (ofrecen servicios), e incluso ambos.

**Atributos comunes:**

* `id`
* `nombre`
* `apellido`
* `correo`
* `teléfono`
* `contraseña` (encriptada)
* `fechaRegistro`
* `rol` (CLIENTE, PROVEEDOR, AMBOS)

---

### 🛠️ Servicio

Cada proveedor puede ofrecer múltiples servicios.

**Atributos:**

* `id`
* `nombre`
* `descripción`
* `tarifa`
* `categoría`
* `proveedorId` (FK)
* `calificaciónPromedio`
* `disponibilidadHoraria` (relación 1 a muchos con `Horario`)

---

### 🗓️ Horario

Define los bloques horarios en los que un servicio está disponible.

**Atributos:**

* `id`
* `díaSemana`
* `horaInicio`
* `horaFin`
* `servicioId` (FK)

---

### 📚 Categoría

Permite clasificar los servicios.

**Atributos:**

* `id`
* `nombre`
* `icono`

---

### 📅 Reserva

Reserva realizada por un cliente para un servicio específico.

**Atributos:**

* `id`
* `fechaReserva`
* `hora`
* `estado` (GENERADO, PAGADO, ACEPTADO, CANCELADO, TERMINADO)
* `clienteId` (FK)
* `servicioId` (FK)
* `canceladoPor` (CLIENTE/PROVEEDOR)
* `fechaCreacion`
* `dirección`

---

### 💳 Pago

Relacionado a una reserva.

**Atributos:**

* `id`
* `reservaId` (FK)
* `monto`
* `fechaPago`
* `estado`
* `métodoPago` (STRIPE, PAYPAL)

---

### 🌟 Reseña

Escrita por el cliente luego de un servicio completado.

**Atributos:**

* `id`
* `reservaId` (FK)
* `puntuación` (1-5)
* `comentario`
* `fechaCreacion`

---

## 🔄 Relaciones Clave

* Un **Proveedor** tiene múltiples **Servicios**.
* Un **Servicio** tiene múltiples **Horarios**.
* Un **Cliente** puede hacer múltiples **Reservas**, cada una vinculada a un **Servicio**.
* Cada **Reserva** tiene un único **Pago**.
* Una **Reseña** es posible sólo si la **Reserva** está en estado **TERMINADO**.

---

## 🌐 Endpoints Principales

### Usuarios

| Método | Endpoint         | Descripción                   |
| ------ | ---------------- | ----------------------------- |
| POST   | `/auth/register` | Registro de cliente/proveedor |
| POST   | `/auth/login`    | Autenticación de usuarios     |

### Servicios

| Método | Endpoint         | Descripción                                                         |
| ------ | ---------------- | ------------------------------------------------------------------- |
| GET    | `/services`      | Buscar servicios con filtros (categoría, ubicación, disponibilidad) |
| POST   | `/services`      | Crear un servicio (Proveedor)                                       |
| GET    | `/services/{id}` | Ver detalles del servicio                                           |

### Reservas

| Método | Endpoint                  | Descripción                                      |
| ------ | ------------------------- | ------------------------------------------------ |
| POST   | `/bookings`               | Crear una reserva (Cliente)                      |
| PATCH  | `/bookings/{id}/status`   | Actualizar estado de reserva (Proveedor/Cliente) |
| GET    | `/bookings/client/{id}`   | Listar reservas por cliente                      |
| GET    | `/bookings/provider/{id}` | Listar reservas por proveedor                    |

### Pagos

| Método | Endpoint                | Descripción              |
| ------ | ----------------------- | ------------------------ |
| POST   | `/payments/checkout`    | Procesar pago de reserva |
| GET    | `/payments/{reservaId}` | Ver estado del pago      |

### Reseñas

| Método | Endpoint                | Descripción                    |
| ------ | ----------------------- | ------------------------------ |
| POST   | `/reviews`              | Publicar reseña                |
| GET    | `/reviews/service/{id}` | Obtener reseñas de un servicio |

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
* DTOs separados para creación y respuesta

---

## ❌ Manejo de Errores

* `404`: Recurso no encontrado
* `409`: Conflictos (doble reserva, usuario existente)
* `400`: Datos inválidos (validados con DTOs)
* `401`: Autenticación fallida
* `500`: Error interno

Controlados globalmente con `@ControllerAdvice`

---

## 🔔 Eventos Asíncronos

* Envío de correo de bienvenida (`SendGrid`)
* Confirmación de pago (`Stripe Webhooks`)
* Notificaciones de reserva aceptada/cancelada (`Twilio SMS / Email`)

---

## 🧪 Testing

* **Unit Test:** lógica de servicios
* **Integration Test:** pagos, autenticación, reservas
* **E2E:** flujo completo: registro → búsqueda → reserva → pago → reseña

---

## ☁️ Integraciones

| Servicio              | Uso                                      |
| --------------------- | ---------------------------------------- |
| Stripe / PayPal       | Procesamiento de pagos                   |
| Google Maps API       | Geolocalización y búsqueda por ubicación |
| Firebase Auth / OAuth | Login con Google o Facebook              |
| SendGrid / Twilio     | Notificaciones por email/SMS             |

---

## 🚀 Despliegue

* Base de datos PostgreSQL
* Docker para entorno local
* Despliegue sugerido en Railway / Render / Vercel (frontend)

---

¿Te gustaría que también genere un diagrama E-R, Swagger UI, o Dockerfile para este proyecto?
