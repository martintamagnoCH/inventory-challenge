# Instrucciones para Ejecutar el Proyecto

## Requisitos Previos

- **Java 17** instalado (`java -version` para verificar)
- **Maven** 3.8+ (opcional: el proyecto incluye el wrapper `./mvnw`)
- No requiere base de datos externa: utiliza **H2 en memoria**

---
## Ejecución de la Aplicación

```bash
./mvnw spring-boot:run
```
---

## Acceso a la API

Una vez iniciado, la aplicación estará corriendo en  
[http://localhost:8080](http://localhost:8080)

- **Swagger UI:**  
  [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

---

## Correr Tests

Para correr todos los tests unitarios:

```bash
./mvnw test
```
---
## Observabilidad y Logs

- Todos los logs se imprimen en consola.
- Los errores de API y de concurrencia quedan registrados automáticamente.
