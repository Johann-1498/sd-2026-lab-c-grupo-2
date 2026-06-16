const express = require('express');
const app = express();

app.use(express.json());

let envios = []; // Base de datos simulada

app.post('/api/transporte/programar', (req, res) => {
    const { pedidoId, usuarioId } = req.body;

    if (!pedidoId || !usuarioId) {
        return res.status(400).json({
            error: "Datos incompletos para programar transporte"
        });
    }

    const envio = {
        id: envios.length + 1,
        pedidoId,
        usuarioId,
        estado: "PROGRAMADO"
    };

    envios.push(envio);

    res.status(201).json({
        mensaje: "Transporte programado correctamente",
        envio
    });
});

app.get('/api/transporte/envios', (req, res) => {
    res.status(200).json(envios);
});

app.listen(3004, () => {
    console.log('Transporte Service corriendo en puerto 3004');
});