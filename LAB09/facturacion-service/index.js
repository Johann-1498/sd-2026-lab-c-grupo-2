const express = require('express');
const { PrismaClient } = require('@prisma/client');
const app = express();
const prisma = new PrismaClient();
app.use(express.json());

app.get('/health', (req, res) => {
    res.status(200).json({ servicio: 'facturacion-service', estado: 'OK' });
});

// Validador de promociones
app.get('/api/promociones/:codigo', (req, res) => {
    const codigo = req.params.codigo;
    if (codigo === 'VERANO20') return res.json({ valido: true, descuento: 0.20 });
    res.json({ valido: false, descuento: 0 });
});

// Creación de factura con Idempotencia (Evita duplicados)
app.post('/api/facturas', async (req, res) => {
    const { pedidoId, total } = req.body;
    
    try {
        const nuevaFactura = await prisma.factura.create({
            data: { pedidoId, total }
        });
        res.status(201).json({ mensaje: "Factura generada", factura: nuevaFactura });
    } catch (error) {
        // En Prisma, el código P2002 indica violación de restricción única
        if (error.code === 'P2002') {
            return res.status(409).json({ error: "Factura ya generada para este pedido" });
        }
        console.error(error);
        res.status(500).json({ error: "Error interno del servidor" });
    }
});

app.listen(3002, () => console.log('Facturación Service corriendo en puerto 3002'));