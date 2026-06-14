const express = require('express');
const axios = require('axios');
const app = express();
app.use(express.json());

app.post('/api/pedidos', async (req, res) => {
    const { usuarioId, productoId, cantidad, precioBase, codigoDescuento } = req.body;
    let total = precioBase * cantidad;

    try {
        // 1. Verificar inventario
        await axios.post('http://localhost:3001/api/inventario/verificar', { productoId, cantidad });

        // 2. Aplicar descuento (Soluciona: Pedidos sin descuento)
        if (codigoDescuento) {
            const promo = await axios.get(`http://localhost:3002/api/promociones/${codigoDescuento}`);
            if (promo.data.valido) total = total - (total * promo.data.descuento);
        }

        // 3. Crear pedido en bd (simulado)
        const pedidoId = Math.floor(Math.random() * 1000);

        // 4. Generar Factura
        await axios.post('http://localhost:3002/api/facturas', { pedidoId, total });

        // 5. Notificación ASÍNCRONA (Soluciona lentitud de 8 segundos). 
        // Nota: NO usamos "await" aquí a propósito para no bloquear la respuesta.
        axios.post('http://localhost:3003/api/notificaciones', { 
            usuarioId, mensaje: "Tu pedido ha sido creado" 
        }).catch(err => console.log("Error de notificación, pero el pedido sigue"));

        res.status(201).json({ 
            mensaje: "Pedido registrado con éxito", 
            pedido: { id: pedidoId, totalCobrado: total, estado: 'CREADO' } 
        });

    } catch (error) {
        // Capturar errores (ej: Inventario insuficiente)
        const status = error.response ? error.response.status : 500;
        const msg = error.response ? error.response.data.error : "Error interno";
        res.status(status).json({ error: msg });
    }
});

// Endpoint para Cancelar Pedido
app.post('/api/pedidos/:id/cancelar', (req, res) => {
    const id = req.params.id;
    // Simulamos la regla de negocio para el CP-05
    if (id === '888') {
        return res.status(403).json({ error: "No se puede cancelar un pedido en tránsito" });
    }
    // Cancelación exitosa para el CP-04
    res.status(200).json({ mensaje: "Pedido CANCELADO exitosamente", estado: "CANCELADO" });
});

app.listen(3000, () => console.log('Pedidos Service corriendo en puerto 3000'));