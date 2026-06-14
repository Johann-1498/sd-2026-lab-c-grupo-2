const express = require('express');
const app = express();
app.use(express.json());

app.post('/api/notificaciones', (req, res) => {
    // Simulamos que encolamos el correo para enviarlo después (asíncrono)
    console.log(`[EMAIL ENVIADO] A usuario ${req.body.usuarioId}: ${req.body.mensaje}`);
    
    // Respondemos INMEDIATAMENTE para que el sistema no sea lento
    res.status(200).json({ mensaje: "Notificación encolada correctamente" });
});

app.listen(3003, () => console.log('Notificación Service corriendo en puerto 3003'));