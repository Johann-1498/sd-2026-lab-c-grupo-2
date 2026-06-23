# Actividad 5: Evaluación Crítica y Propuesta de Mejoras de Resiliencia

Tras ejecutar el ciclo completo de inyección de fallos (`fallo-lima.sh`), auditoría transaccional (`Postman`) y recuperación del servicio (`restaurar-lima.sh`), se identifican tres brechas críticas en el estado actual de la infraestructura distribuida de FedEx Perú. Se proponen las siguientes mejoras de arquitectura e ingeniería de software:

## 1. Implementación de Failover Automático y Orquestación (Alta Disponibilidad Real)
- **Deficiencia Actual:** El proceso de conmutación de tráfico hacia el nodo de Bogotá es manual. Requiere que un administrador modifique el archivo `docker-compose.yml` o reinicie el contenedor del Backend API cambiando la variable `DATABASE_URL`. Esto eleva drásticamente el RTO (Recovery Time Objective).
- **Solución Propuesta:** Integrar herramientas nativas de orquestación de bases de datos como **repmgr** (PostgreSQL Replication Manager) o un proxy intermedio como **HAProxy** / **PgBouncer**. Al configurar *health checks* continuos cada 2 segundos, si HAProxy detecta el fallo de Lima, redirigirá el tráfico de la API de manera transparente hacia Bogotá en milisegundos, reduciendo la intervención humana a cero.

## 2. Arquitectura de Separación de Lecturas y Escrituras (Read-Splitting)
- **Deficiencia Actual:** El backend actual está rígidamente conectado a una sola base de datos a la vez a través de SQLAlchemy. Durante la operación normal, el nodo de Lima procesa tanto las masivas consultas de tracking de los clientes como los registros de nuevos envíos, desaprovechando el potencial de procesamiento de las réplicas regionales.
- **Solución Propuesta:** Modificar la lógica de inyección de sesiones en FastAPI (`database.py` y `get_db()`). Se debe configurar un enrutador de base de datos dinámico (*Database Router*):
  - Todas las operaciones de mutación de datos (`POST`, `PUT`, `DELETE`) serán enviadas por defecto al nodo primario (`postgres-lima`).
  - Todas las peticiones de consulta masiva (`GET`) serán distribuidas mediante un algoritmo Round-Robin o por cercanía geográfica entre las réplicas de Bogotá, Santiago y México, optimizando drásticamente el rendimiento de red internacional de FedEx.

## 3. Implementación de Monitoreo Centralizado y Circuit Breakers
- **Deficiencia Actual:** Si la base de datos de Lima se cae, las peticiones HTTP entrantes al backend se quedan colgadas esperando un *timeout* de la conexión TCP de la base de datos, lo que degrada la experiencia del usuario y puede agotar los hilos de ejecución de la API (efecto dominó).
- **Solución Propuesta:** 
  - **Circuit Breaker (Disyuntor):** Implementar el patrón *Circuit Breaker* en el backend API. Si la conexión a la base de datos principal falla 3 veces seguidas, el circuito se "abre" e inmediatamente desvía el tráfico de lectura a las réplicas locales o devuelve una respuesta degradada elegante, sin colapsar el backend.
  - **Monitoreo:** Agregar un stack de telemetría liviano con **Prometheus** y **Grafana**, utilizando el exportador `postgres_exporter` en cada contenedor. Esto permitirá disparar alertas automáticas a canales de soporte (Slack/PagerDuty) en el instante exacto en que una réplica o el nodo principal experimenten degradación de rendimiento o desconexión.