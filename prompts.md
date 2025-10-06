# prompts

- Actualmente, para mantener el inventario en diferentes sucursales, cada sucursal cuenta con una base de datos local qeu se sincroniza cada 15 minutos con una base de datos central.
Podés proponer una arquitectura distribuida para optimizar el mantenimiento de la consistencia de los inventarios, reducienod la latencia de actualización?

- me proponés los endpoins que debería considerara para que cada aplicación de sucursal pueda consultar el inventario actual, y notificar los movimientos?
Me gustaría que la API considera actualizaciones on line para operaciones críticas, pero también pueda aceptar actualizaciones a través de colas de mensajería.

- qué estrategias me proponés para mitigar race conditions en casos de concurrencia de operaciones de actualización dle inventario?