# Actividad 2: Diseño Arquitectónico

## 1. Descripción de la Arquitectura
Para satisfacer las necesidades de FedEx Perú, hemos diseñado una arquitectura de sistemas distribuidos basada en el patrón **Primary-Replica** con un enfoque de **Replicación Híbrida** para equilibrar consistencia y rendimiento.

- **Nodo Principal (Primary):** `postgres-lima`. Es el único nodo que recibe operaciones de escritura (POST/PUT) desde el backend API. 
- **Nodos Secundarios (Read Replicas):** `postgres-bogota`, `postgres-santiago`, y `postgres-mexico`. Reciben los datos de Lima y sirven para escalar las consultas (GET) en sus respectivas regiones geográficas.
- **Failover (Recuperación ante fallos):** Bogotá se establece como réplica prioritaria. Si el nodo principal en Lima falla, el tráfico se redirigirá a Bogotá.

## 2. Justificación Técnica
Se eligió esta arquitectura por las siguientes razones:

1. **Balanceo de Carga (Lectura vs Escritura):** En FedEx, el volumen de consultas de estado de envíos es inmensamente mayor a las escrituras. Delegar las lecturas a los nodos locales (Bogotá, Santiago, México) reduce la latencia regional y evita que el nodo de Lima colapse.
2. **Replicación Híbrida (Estrategia selectiva):**
   - *Datos Críticos (Inventarios y Pedidos):* Requieren replicación **Síncrona** (o semi-síncrona). Es imperativo que no se pierdan datos de facturación si Lima cae.
   - *Datos de Seguimiento (Temperaturas y Ubicación GPS de Vehículos):* Requieren replicación **Asíncrona**. Son ráfagas masivas de datos continuos; forzar sincronía aquí ralentizaría toda la red. Perder 1 minuto de ubicación ante un desastre es un riesgo aceptable.
3. **Continuidad del Negocio:** Docker nos permite simular el ecosistema. Si detenemos `postgres-lima`, el orquestador (o el DBA) puede promover `postgres-bogota` para que asuma el servicio minimizando el tiempo de inactividad (RTO).