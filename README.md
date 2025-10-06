# Challenge – API para de Gestión de Inventario

## Resumen

Este proyecto es un prototipo de backend para un sistema distribuido de gestión de inventario.  
El objetivo es **optimizar la consistencia del inventario**, **reducir la latencia de actualización de stock** y **bajar los costos operativos**, asegurando buenas prácticas en errores, seguridad y observabilidad.

---

## Arquitectura Propuesta

- **Backend basado en microservicios:** Cada tienda y el sistema central se comunican vía APIs.
- **API central de inventario:** Recibe las consultas y actualizaciones de stock. En un entorno real, sería un servicio escalable y sin estado detrás de un balanceador de carga.
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
### Actualización de Stock: Sincrónica y Asincrónica

El sistema admite **actualizaciones de inventario tanto en modo sincrónico como asincrónico**, permitiendo máxima flexibilidad y resiliencia para las tiendas.

- **Sincrónica (API directa):**  
  Las tiendas pueden actualizar el stock llamando directamente al endpoint `/inventory/update`. La API procesa el pedido y responde de inmediato con el resultado (éxito o error).  
  Este modo garantiza consistencia fuerte, ideal para operaciones críticas como ventas online.

- **Asincrónica (mensajería/eventos):**  
  Alternativamente, las tiendas pueden publicar pedidos de actualización en una cola de mensajería (RabbitMQ, SQS, etc).  
  La API central consume estos mensajes y responde 200 OK si el mensaje fue aceptado en la cola, o 4xx/5xx en caso de error. En este último caso, la cola de mensajería se encargará de reencolar y reintentar.

> “Se soportan ambos modos para maximizar la robustez y la escalabilidad del sistema. La actualización sincrónica garantiza respuesta inmediata y consistencia fuerte cuando es necesario (ej. ventas). La asincrónica, basada en mensajería, permite operar incluso si el backend central está temporalmente caído, logrando consistencia eventual y tolerancia a fallos avanzada. El sistema es flexible para adaptarse a ambos escenarios según la criticidad o el volumen de la operación.”
---

## Decisiones Técnicas Clave

- **Consistencia por sobre Disponibilidad:**  
  Se utiliza locking optimista (`@Version` en JPA) en las actualizaciones de stock para evitar sobreventa y garantizar integridad.  
  Si dos operaciones intentan modificar el mismo registro, una fallará con error `409 Conflict`, evitando corrupción de datos y las mismas puedan ser reintentadas.
- **API-Driven:**  
  Todas las operaciones de inventario están expuestas vía API REST, facilitando la integración con sistemas de tienda y frontend web.
- **Observabilidad y Tolerancia a Fallos:**  
  Un handler global unifica las respuestas de error en JSON y registra todos los eventos importantes para observabilidad. La API responde con mensajes de error estructurados y claros. Los conflictos de concurrencia se manejan de de la forma descripta en el primer punto.


## Endpoints Principales

| Método | Endpoint                      | Descripción                                     |
|--------|-------------------------------|-------------------------------------------------|
| GET    | `/inventory`                  | Lista todo el inventario global                 |
| GET    | `/inventory/{sku}`            | Consulta inventario por SKU                     |
| POST   | `/inventory/update`           | Actualiza el stock de un SKU/tienda             |
| POST   | `/inventory/movement`         | Registra movimiento de stock (venta/reposición) |
| GET    | `/swagger-ui.html`            | Documentación interactiva (Swagger UI)          |

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