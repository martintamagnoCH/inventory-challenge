# Challenge – API para de Gestión de Inventario

## Resumen

Este proyecto es un prototipo de backend para un sistema distribuido de gestión de inventario.  
El objetivo es **optimizar la consistencia del inventario**, **reducir la latencia de actualización de stock** y **bajar los costos operativos**, asegurando buenas prácticas en errores, seguridad y observabilidad.

---

## Arquitectura Propuesta

**[Agrega un diagrama aquí si lo deseas, o describe en texto.]**

- **Backend basado en microservicios:** Cada tienda y el sistema central se comunican vía APIs en (casi) tiempo real.
- **API central de inventario:** Recibe todas las consultas y actualizaciones de stock. En un entorno real, sería un servicio escalable y sin estado detrás de un balanceador de carga.
- **Base de datos:** Este prototipo usa H2 en memoria para simular persistencia. En producción se recomienda una base distribuida SQL/NoSQL para escalabilidad y confiabilidad.
- **Estrategia de consistencia:** Las actualizaciones de stock usan locking optimista para evitar condiciones de carrera y asegurar datos correctos, incluso bajo operaciones concurrentes.

### Diagrama de Arquitectura (Ejemplo)

```
[App Tienda] --\
                \
[App Tienda] ----> [Servicio API Inventario] <---- [Tienda Online/Web]
                /
[App Tienda] --/
                   |
              [DB Distribuida]
```

---

## Decisiones Técnicas Clave

- **Consistencia por sobre Disponibilidad:**  
  Se utiliza locking optimista (`@Version` en JPA) en las actualizaciones de stock para evitar sobreventa y garantizar integridad, incluso si algunas operaciones concurrentes fallan (pueden ser reintentadas).
- **API-Driven:**  
  Todas las operaciones de inventario están expuestas vía API REST, facilitando la integración con sistemas de tienda y frontend web.
- **Observabilidad y Tolerancia a Fallos:**  
  Todos los errores y problemas de validación se loguean. La API responde con mensajes de error estructurados y claros. Los conflictos de concurrencia se manejan de forma amigable.

---

## Endpoints Principales

| Método | Endpoint                      | Descripción                                      |
|--------|-------------------------------|--------------------------------------------------|
| GET    | `/inventory`                  | Lista todo el inventario global                  |
| GET    | `/inventory/{sku}`            | Consulta inventario por SKU                      |
| POST   | `/inventory/update`           | Actualiza el stock de un SKU/tienda (directo)    |
| POST   | `/inventory/movement`         | Registra movimiento de stock (venta/reposición)  |
| GET    | `/swagger-ui.html`            | Documentación interactiva (Swagger UI)           |

*Todos los endpoints retornan errores en formato JSON consistente, con timestamp y detalles.*

---

## Stack Tecnológico

- **Java 17**
- **Spring Boot**
- **Spring Data JPA**
- **Base de datos H2** (en memoria, solo para prototipo)
- **Lombok**
- **Swagger/OpenAPI** para documentación

---

## Cómo Ejecutar el Proyecto

Ver [`run.md`](./run.md) para instrucciones detalladas.

**Inicio rápido:**
```bash
git clone https://github.com/martintamagnoCH/inventory-challenge.git
cd inventory-challenge
./mvnw spring-boot:run
```
Acceso a Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

---

## Concurrencia y Tolerancia a Fallos

- **Locking optimista:**  
  Todas las operaciones de actualización de stock son transaccionales y usan locking optimista (`@Version` en la entidad `Inventory`).  
  Si dos operaciones intentan modificar el mismo registro, una fallará con error `409 Conflict`, evitando corrupción de datos.

- **Manejo de Errores:**  
  Un handler global unifica las respuestas de error en JSON y registra todos los eventos importantes para observabilidad.

---

## Observabilidad

- Todos los errores y advertencias se registran vía SLF4J.
- La API expone mensajes claros para errores de validación y concurrencia.

---

## Mejoras Futuras

- Migración a una base de datos distribuida real (ej: CockroachDB, CosmosDB).
- Sincronización por eventos (ej: Kafka, RabbitMQ) entre tiendas y centro.
- Autenticación y autorización.
- Métricas y monitoreo avanzado.

---

## Uso de Herramientas Modernas

Este proyecto se desarrolló utilizando GitHub Copilot y ChatGPT para sugerencias de código, buenas prácticas y documentación.  
Ver [`prompts.md`](./prompts.md) para los prompts e interacciones AI utilizadas.

---

## Autor

Martín Tamagno

---