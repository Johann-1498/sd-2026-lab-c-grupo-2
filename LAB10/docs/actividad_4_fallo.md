# Actividad 4: Simulación de Fallo y Resiliencia en FedEx Perú

## 1. Descripción del Flujo de Fallo y Conmutación (Failover)
Para demostrar la alta disponibilidad de la solución diseñada para FedEx Perú, se diseñó un escenario de desastre controlado utilizando el script automatizado `fallo-lima.sh`.

El flujo exacto ejecutado es el siguiente:
1. **Detención de la Base de Datos Principal:** El script ejecuta de forma forzada un comando de detención estructural (`docker stop fedex_db_lima`), simulando una caída completa del centro de datos de Lima por un periodo de 20 minutos.
2. **Monitoreo de Infraestructura:** El script muestra en consola el estado actual de los contenedores mediante `docker ps`, evidenciando que `fedex_db_lima` se encuentra inactivo, mientras que las réplicas (`fedex_db_bogota`, `fedex_db_santiago`, `fedex_db_mexico`) siguen operativas.
3. **Conmutación Manual del Backend (Failover):** Al no contar con un orquestador automatizado en esta fase, se levanta una instancia del backend API redirigiendo la variable de entorno `DATABASE_URL` hacia la réplica prioritaria elegida en el diseño arquitectónico: `postgres-bogota`.

## 2. Resultados de las Pruebas de Continuidad con Postman
Utilizando la colección automatizada `FedEx_Replicacion.postman_collection.json`, se auditaron las fases de la contingencia con los siguientes resultados:

- **Fase de Caída Activa (Sin Failover):** Al disparar las peticiones a los endpoints (`/health`, `/inventarios`, `/pedidos`) apuntando a Lima caída, el backend FastAPI arroja errores controlados (HTTP 500 o fallos de conexión por timeout), justificando la interrupción operativa inmediata del nodo.
- **Fase de Failover a Bogotá:** Una vez conectado el backend a `postgres-bogota`, las pruebas automatizadas de Postman validan el cumplimiento de la resiliencia:
  - **Validación de Datos:** Las peticiones `GET /inventarios` y `GET /pedidos` devuelven respuestas HTTP 200 exitosas. Las aserciones (`pm.test`) demuestran que Bogotá preserva exactamente el mismo número total de registros previamente insertados en Lima, gracias al mecanismo de replicación.
  - **Capacidad de Escritura:** Se ejecutó con éxito un `POST /pedidos` de emergencia directamente en el nodo de Bogotá, demostrando que este nodo asume temporalmente el rol principal de lectura y escritura para salvar la continuidad del negocio de FedEx.

## 3. Impacto de Pérdida de Datos según el Tipo de Replicación
Tomando como referencia la matriz de sincronización definida por el equipo, se analiza el comportamiento transaccional ante el desastre:

- **Impacto bajo Replicación Asíncrona (Temperaturas, Inventarios, Vehículos):** Dado que la replicación asíncrona no bloquea el nodo primario, si ocurrieron actualizaciones de coordenadas GPS de camiones (`vehiculos`) o alertas críticas en cámaras de frío (`temperaturas`) milisegundos antes del apagón de Lima y no llegaron a procesarse en el script de réplica, **estos datos se pierden**. No obstante, al ser telemetría de alta frecuencia, es una pérdida mitigable y aceptable para la operación logística.
- **Impacto bajo Replicación Síncrona Estricta (Historial de Pedidos):** Para el módulo de pedidos, la sincronía garantiza que un registro no se da por exitoso en el cliente si no ha sido confirmado por la réplica. Al caer Lima, se garantiza un RPO = 0 (cero pérdida de datos financieros), asegurando que todo pedido visualizado en Bogotá es idéntico al último estado de Lima.