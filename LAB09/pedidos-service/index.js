const express = require('express');
const axios = require('axios');

const app = express();
app.use(express.json());

const AXIOS_TIMEOUT = 3000; // 3 segundos
const MAX_RETRIES = 2;

// Espera simple para reintentos
function sleep(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}

// Función reutilizable para llamadas resilientes entre microservicios
async function requestWithRetry(config, serviceName) {
    for (let intento = 1; intento <= MAX_RETRIES + 1; intento++) {
        try {
            return await axios({
                timeout: AXIOS_TIMEOUT,
                ...config
            });
        } catch (error) {
            // Si es error de negocio, no reintentamos.
            // Ejemplo: inventario insuficiente = 400
            if (error.response && error.response.status < 500) {
                error.serviceName = serviceName;
                throw error;
            }

            // Si ya agotó los intentos, lanzamos el error
            if (intento === MAX_RETRIES + 1) {
                error.serviceName = serviceName;
                throw error;
            }

            console.log(`[RETRY] ${serviceName} falló en intento ${intento}. Reintentando...`);
            await sleep(500 * intento);
        }
    }
}

// Ruta de prueba para verificar que pedidos-service está activo
app.get('/health', (req, res) => {
    res.status(200).json({
        servicio: "pedidos-service",
        estado: "OK"
    });
});

app.post('/api/pedidos', async (req, res) => {
    const { usuarioId, productoId, cantidad, precioBase, codigoDescuento } = req.body;

    const INVENTARIO_URL   = process.env.INVENTARIO_URL   || 'http://localhost:3001';
    const FACTURACION_URL  = process.env.FACTURACION_URL  || 'http://localhost:3002';
    const NOTIFICACION_URL = process.env.NOTIFICACION_URL || 'http://localhost:3003';
    const TRANSPORTE_URL   = process.env.TRANSPORTE_URL   || 'http://localhost:3004';

    let total = precioBase * cantidad;

    try {
        // 1. Verificar inventario
        await requestWithRetry({
            method: 'post',
            url: `${INVENTARIO_URL}/api/inventario/verificar`,
            data: {
                productoId,
                cantidad
            }
        }, "Inventario");

        // 2. Aplicar descuento
        if (codigoDescuento) {
            const promo = await requestWithRetry({
                method: 'get',
                url: `${FACTURACION_URL}/api/promociones/${codigoDescuento}`
            }, "Facturación - Promociones");

            if (promo.data.valido) {
                total = total - (total * promo.data.descuento);
            }
        }

        // 3. Crear pedido en BD simulada
        const pedidoId = Math.floor(Math.random() * 1000);

        // 4. Generar factura
        await requestWithRetry({
            method: 'post',
            url: `${FACTURACION_URL}/api/facturas`,
            data: {
                pedidoId,
                total
            }
        }, "Facturación");

        // 5. Programar transporte
        const transporteResponse = await requestWithRetry({
            method: 'post',
            url: `${TRANSPORTE_URL}/api/transporte/programar`,
            data: {
                pedidoId,
                usuarioId
            }
        }, "Transporte");

        // 6. Notificación asíncrona
        axios.post(`${NOTIFICACION_URL}/api/notificaciones`, {
            usuarioId,
            mensaje: "Tu pedido ha sido creado y el transporte fue programado"
        }, {
            timeout: 2000
        }).catch(() => {
            console.log("Error de notificación, pero el pedido sigue");
        });

        // 7. Respuesta final al cliente
        res.status(201).json({
            mensaje: "Pedido registrado con éxito",
            pedido: {
                id: pedidoId,
                totalCobrado: total,
                estado: "CREADO",
                transporte: transporteResponse.data.envio
            }
        });

    } catch (error) {
        let status = 500;
        let msg = "Error interno o servicio no disponible";

        if (error.response) {
            status = error.response.status;
            msg = error.response.data.error || error.response.data.mensaje || "Error en servicio externo";
        } else if (error.code === 'ECONNABORTED') {
            msg = `Tiempo de espera agotado en el servicio de ${error.serviceName || 'servicio externo'}`;
        } else {
            msg = `Fallo de comunicación con el servicio de ${error.serviceName || 'servicio externo'}`;
        }

        res.status(status).json({
            error: msg
        });
    }
});

// Endpoint para cancelar pedido
app.post('/api/pedidos/:id/cancelar', (req, res) => {
    const id = req.params.id;

    if (id === '888') {
        return res.status(403).json({
            error: "No se puede cancelar un pedido en tránsito"
        });
    }

    res.status(200).json({
        mensaje: "Pedido CANCELADO exitosamente",
        estado: "CANCELADO"
    });
});

app.listen(3000, () => {
    console.log('Pedidos Service corriendo en puerto 3000');
});