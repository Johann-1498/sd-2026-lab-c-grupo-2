const express = require('express');
const cors = require('cors');
const { PrismaClient } = require('@prisma/client');
const app = express();
app.use(cors());
const prisma = new PrismaClient();

app.use(express.json());

app.get('/health', (req, res) => {
    res.status(200).json({ servicio: 'transporte-service', estado: 'OK' });
});

app.post('/api/transporte/programar', async (req, res) => {
    const { pedidoId, usuarioId } = req.body;

    if (!pedidoId || !usuarioId) {
        return res.status(400).json({
            error: "Datos incompletos para programar transporte"
        });
    }

    try {
        const nuevoEnvio = await prisma.envio.create({
            data: {
                pedidoId,
                usuarioId,
                estado: "PROGRAMADO"
            }
        });

        res.status(201).json({
            mensaje: "Transporte programado exitosamente",
            envio: nuevoEnvio
        });
    } catch (error) {
        console.error(error);
        res.status(500).json({ error: "Error interno del servidor" });
    }
});

app.get('/api/transporte/envios', async (req, res) => {
    const envios = await prisma.envio.findMany();
    res.status(200).json(envios);
});

app.listen(3004, () => {
    console.log('Transporte Service corriendo en puerto 3004');
});