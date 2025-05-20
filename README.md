# Marketplace de Servicios Domésticos

## Descripción General

El **Marketplace de Servicios Domésticos** es una plataforma digital diseñada para conectar a usuarios con profesionales de servicios domésticos, como plomeros, electricistas, personal de limpieza y jardineros. Su objetivo principal es simplificar el proceso de búsqueda, contratación y pago de estos servicios, brindando una solución eficiente, segura y accesible tanto para clientes como para proveedores.

## Contexto del Negocio

La plataforma funcionará como un intermediario que:

1. Registra y autentica a clientes y proveedores de servicios.
2. Permite a los proveedores publicar sus servicios y gestionar su disponibilidad.
3. Facilita a los clientes la búsqueda, reserva y pago de servicios.
4. Gestiona reseñas y calificaciones para garantizar transparencia y confianza.
5. Proporciona herramientas de administración para gestionar usuarios, servicios y disputas.

## Tecnologías Utilizadas

### Backend
- **Java 17+**
- **Spring Boot 3.x**
- **Spring Security con JWT**
- **Spring Data JPA**
- **PostgreSQL**

### Integraciones con Servicios de Terceros
- **Stripe/PayPal**: Para procesamiento de pagos.
- **Google Maps API**: Para geolocalización y búsqueda de servicios cercanos.
- **Twilio/SendGrid**: Para notificaciones por SMS y correo electrónico.
- **Firebase Auth/OAuth**: Para autenticación mediante redes sociales.

### Frontend (Opcional para MVP)
- **React.js** o **Angular**
- **Bootstrap** para diseño responsivo.

### Testing
- **JUnit** y **Mockito** para pruebas unitarias y de integración.

## Entidades y Relaciones

El sistema modela las siguientes entidades y relaciones:

1. **Cliente**: Usuarios que contratan servicios. Atributos: ID, nombre, apellido, correo, teléfono, contraseña (encriptada), dirección, fecha de registro.
2. **Proveedor**: Usuarios que ofrecen servicios. Atributos: ID, nombre, apellido, correo, teléfono, contraseña (encriptada), fecha de registro, foto de perfil, calificación promedio.
3. **Servicio**: Oferta de trabajo publicada por un proveedor. Atributos: ID, nombre, descripción, tarifa, categoría, disponibilidad.
4. **Horario**: Franjas horarias disponibles para un servicio. Atributos: ID, día de la semana, hora de inicio, hora de fin.
5. **Categoría**: Clasificación de servicios. Atributos: ID, nombre, icono.
6. **Reserva**: Agenda creada por un cliente. Atributos: ID, fecha, hora, dirección, estado (generado, pagado, aceptado, cancelado, terminado).
7. **Pago**: Transacción asociada a una reserva. Atributos: ID, monto, método de pago, fecha, estado.
8. **Reseña**: Comentario y calificación dejados por un cliente. Atributos: ID, fecha de creación, puntuación, comentario.

### Relaciones Principales
- Un **Cliente** puede realizar múltiples **Reservas**.
- Un **Proveedor** puede ofrecer múltiples **Servicios**.
- Un **Servicio** puede tener múltiples **Horarios**.
- Cada **Reserva** genera un **Pago** y puede tener una **Reseña**.

## Funcionalidades Requeridas

### 1. Gestión de Usuarios
- Registro y autenticación para clientes y proveedores.
- Perfiles verificables para proveedores.
- Roles: Cliente, Proveedor, Administrador.

### 2. Gestión de Servicios
- Publicación y edición de servicios por parte de proveedores.
- Búsqueda y filtrado de servicios por categoría, ubicación y disponibilidad.
- Gestión de horarios y disponibilidad.

### 3. Reservas y Pagos
- Agendamiento de citas en tiempo real.
- Procesamiento de pagos mediante Stripe/PayPal.
- Confirmación y cancelación de reservas.

### 4. Reseñas y Calificaciones
- Sistema de reseñas para clientes.
- Cálculo de calificación promedio para proveedores.

### 5. Administración
- Panel de control para moderación de usuarios y servicios.
- Reportes de actividad y métricas.

## Endpoints Principales

### Autenticación y Usuarios
| Método | Endpoint                     | Descripción                                  |
|--------|------------------------------|----------------------------------------------|
| POST   | `/api/auth/register`         | Registro de cliente o proveedor.             |
| POST   | `/api/auth/login`            | Inicio de sesión.                            |
| GET    | `/api/users/profile`         | Obtener perfil del usuario.                  |
| PUT    | `/api/users/profile`         | Actualizar perfil del usuario.               |

### Servicios
| Método | Endpoint                     | Descripción                                  |
|--------|------------------------------|----------------------------------------------|
| GET    | `/api/services`              | Listar servicios con filtros.               |
| POST   | `/api/services`              | Crear un nuevo servicio (Proveedor).        |
| PUT    | `/api/services/{id}`         | Actualizar un servicio (Proveedor).         |
| DELETE | `/api/services/{id}`         | Eliminar un servicio (Proveedor).           |

### Reservas
| Método | Endpoint                     | Descripción                                  |
|--------|------------------------------|----------------------------------------------|
| POST   | `/api/bookings`              | Crear una reserva (Cliente).                |
| GET    | `/api/bookings`              | Listar reservas del usuario.                |
| PUT    | `/api/bookings/{id}/status`  | Actualizar estado de reserva (Proveedor).   |

### Pagos
| Método | Endpoint                     | Descripción                                  |
|--------|------------------------------|----------------------------------------------|
| POST   | `/api/payments`              | Procesar un pago (Cliente).                 |
| GET    | `/api/payments/{id}`         | Obtener detalles de un pago.                |

### Reseñas
| Método | Endpoint                     | Descripción                                  |
|--------|------------------------------|----------------------------------------------|
| POST   | `/api/reviews`               | Crear una reseña (Cliente).                 |
| GET    | `/api/reviews/{serviceId}`   | Listar reseñas de un servicio.              |

## Seguridad
- Autenticación mediante JWT.
- Roles: `ROLE_CLIENT`, `ROLE_PROVIDER`, `ROLE_ADMIN`.
- Validación de entradas y manejo centralizado de excepciones.

## Testing
- Pruebas unitarias para servicios y controladores.
- Pruebas de integración para flujos críticos (reservas, pagos).
- Pruebas end-to-end para experiencia de usuario.

## Entregables
1. Código fuente completo en un repositorio público de GitHub.
2. Colección de Postman para probar los endpoints (en formato JSON en el root del repositorio).
3. Documentación técnica y guía de despliegue.

## Fases de Desarrollo
1. **Fase 1**: Estructura base (backend, autenticación, base de datos).
2. **Fase 2**: Funcionalidades críticas (búsqueda, reservas, pagos).
3. **Fase 3**: Refinamiento (reseñas, notificaciones, pruebas).
4. **Fase 4**: MVP funcional (despliegue en la nube).

---

**Equipo:**  
Rafael Oliverth Solier Soto  
Yulinio Zavala Mariño  
Christian Hernan Mar Carrillo  
Saul Alejandro Baltazar Palomino  
