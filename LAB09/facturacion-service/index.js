const express = require('express');
const app = express();
app.use(express.json());

let facturas = []; // Base de datos simulada

// Validador de promociones
app.get('/api/promociones/:codigo', (req, res) => {
    const codigo = req.params.codigo;
    if (codigo === 'VERANO20') return res.json({ valido: true, descuento: 0.20 });
    res.json({ valido: false, descuento: 0 });
});

// Creación de factura con Idempotencia (Evita duplicados)
app.post('/api/facturas', (req, res) => {
    const { pedidoId, total } = req.body;
    
    // Verificar si ya existe factura para este pedido
    const existe = facturas.find(f => f.pedidoId === pedidoId);
    if (existe) return res.status(409).json({ error: "Factura ya generada para este pedido" });

    const nuevaFactura = { id: facturas.length + 1, pedidoId, total };
    facturas.push(nuevaFactura);
    res.status(201).json({ mensaje: "Factura generada", factura: nuevaFactura });
});

app.listen(3002, () => console.log('Facturación Service corriendo en puerto 3002'));