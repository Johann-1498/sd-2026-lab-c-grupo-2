# Actividad 3: Selección del Tipo de Replicación

## Tabla Comparativa de Estrategias de Replicación

| Tipo de Dato | Mecanismo Recomendado | Justificación (Consistencia, Disponibilidad, Rendimiento) |
|:---|:---|:---|
| **Inventarios** | **Asíncrona** (con sincronización programada cada 5 minutos) | **Consistencia:** Baja prioridad. Pequeñas diferencias temporales (1-5 minutos) son aceptables en un negocio de logística. <br> **Disponibilidad:** Alta. Las escrituras no dependen de las réplicas. <br> **Rendimiento:** Alto. Permite registrar ventas rápidamente sin esperar confirmaciones. |
| **Seguimiento de Envíos** | **Asíncrona** (con prioridad alta) | **Consistencia:** Media. Los clientes pueden tolerar un retraso de 1-2 minutos en la actualización de la ubicación. <br> **Disponibilidad:** Crítica. El sistema debe seguir funcionando aunque una réplica falle. <br> **Rendimiento:** Alto. Se prioriza la rápida actualización del estado del envío. |
| **Historial de Pedidos** | **Síncrona** (o Semi-síncrona) | **Consistencia:** Crítica. Un pedido confirmado no se puede perder bajo ninguna circunstancia. <br> **Disponibilidad:** Media. Se sacrifica un poco para garantizar la durabilidad del dato. <br> **Rendimiento:** Menor, pero es un costo aceptable por la seguridad del dato. |
| **Reportes Ejecutivos** | **Asíncrona** (Replicación parcial) | **Consistencia:** Baja. Los reportes no requieren datos en tiempo real (pueden ser diarios). <br> **Disponibilidad:** Alta. No impacta las operaciones transaccionales. <br> **Rendimiento:** Alto. Se puede consultar desde réplicas sin afectar el nodo principal. |
| **Temperaturas** | **Asíncrona** (con sincronización cada minuto) | **Consistencia:** Media. Es importante tener datos precisos para control de calidad. <br> **Disponibilidad:** Alta. <br> **Rendimiento:** Alto. Las lecturas son frecuentes pero no requieren confirmación inmediata. |

## Análisis Detallado

### Por qué No usar Síncrona para Todo

1. **Impacto en Rendimiento:** La replicación síncrona multiplica el tiempo de respuesta. Si una transacción tarda 10ms en Lima, con 3 réplicas tardaría al menos 40ms + latencia de red.

2. **Disponibilidad Reducida:** Si una réplica (ej. México) falla, todo el sistema se bloquea. Esto es inaceptable para FedEx.

3. **Costo de Infraestructura:** Requiere conexiones de red ultra-rápidas y estables entre países, lo cual es muy costoso.

### Estrategia Híbrida Recomendada

Para FedEx Perú, recomiendo una **estrategia híbrida**:

- **Síncrona:** Solo para pedidos confirmados (datos críticos)
- **Asíncrona:** Para inventarios, envíos, temperaturas y vehículos
- **Replicación Parcial:** Reportes ejecutivos en una réplica dedicada

### Beneficios de esta Estrategia

✅ **Balance perfecto:** Seguridad para datos críticos y rendimiento para el resto
✅ **Alta disponibilidad:** Si una réplica falla, solo los pedidos síncronos se ven afectados
✅ **Costo optimizado:** No necesita infraestructura de red de alto costo para todos los datos
✅ **Escalable:** Puede añadir más réplicas fácilmente sin degradar el rendimiento

## Conclusión

La replicación no es "one size fits all". Cada tipo de dato tiene requisitos diferentes de consistencia, disponibilidad y rendimiento. Una arquitectura bien diseñada debe aplicar el mecanismo de replicación adecuado a cada caso de uso.