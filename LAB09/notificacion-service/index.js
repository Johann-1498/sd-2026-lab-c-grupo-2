const express = require('express');
const { Worker } = require('bullmq');

const app = express();
app.use(express.json());

app.get('/health', (req, res) => {
    res.status(200).json({ servicio: 'notificacion-service', estado: 'OK' });
});

// Endpoint por si acaso, aunque ahora trabajamos con cola
app.post('/api/notificaciones', (req, res) => {
    console.log(`[HTTP EMAIL ENVIADO] A usuario ${req.body.usuarioId}: ${req.body.mensaje}`);
    res.status(200).json({ mensaje: "Notificación procesada HTTP" });
});

const worker = new Worker('Notificaciones', async job => {
    console.log(`[EMAIL ENVIADO] Procesando trabajo ${job.id}: A usuario ${job.data.usuarioId}: ${job.data.mensaje}`);
    // Simular envío
    await new Promise(resolve => setTimeout(resolve, 500));
}, { connection: { url: process.env.REDIS_URL || 'redis://localhost:6379' } });

worker.on('completed', job => {
    console.log(`Trabajo de notificación ${job.id} completado.`);
});

worker.on('failed', (job, err) => {
    console.log(`Trabajo de notificación ${job.id} falló: ${err.message}`);
});

app.listen(3003, () => console.log('Notificación Service corriendo en puerto 3003 (Con Worker de Redis)'));