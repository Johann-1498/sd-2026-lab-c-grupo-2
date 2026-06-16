const express = require('express');
const { PrismaClient } = require('@prisma/client');
const app = express();
const prisma = new PrismaClient();
app.use(express.json());

app.get('/health', (req, res) => {
    res.status(200).json({ servicio: 'inventario-service', estado: 'OK' });
});

app.post('/api/inventario/verificar', async (req, res) => {
    const { productoId, cantidad } = req.body;
    
    try {
        const producto = await prisma.inventario.findUnique({ where: { id: productoId } });
        
        if (!producto) return res.status(404).json({ error: "Producto no existe" });
        if (producto.stock < cantidad) return res.status(400).json({ error: "Inventario insuficiente" });
        
        // Descontamos stock usando transacción implícita de Prisma (update con decrement)
        const updated = await prisma.inventario.update({
            where: { id: productoId },
            data: { stock: { decrement: cantidad } }
        });
        
        res.status(200).json({ mensaje: "Stock descontado exitosamente", stockRestante: updated.stock });
    } catch (error) {
        console.error(error);
        res.status(500).json({ error: "Error interno del servidor" });
    }
});

app.listen(3001, () => console.log('Inventario Service corriendo en puerto 3001'));