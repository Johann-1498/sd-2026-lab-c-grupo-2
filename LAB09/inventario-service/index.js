const express = require('express');
const app = express();
app.use(express.json());

// Simulamos base de datos
let inventario = [
    { id: 1, nombre: 'Manzanas', stock: 10 },
    { id: 2, nombre: 'Leche', stock: 5 }
];

app.post('/api/inventario/verificar', (req, res) => {
    const { productoId, cantidad } = req.body;
    const producto = inventario.find(p => p.id === productoId);
    
    if (!producto) return res.status(404).json({ error: "Producto no existe" });
    if (producto.stock < cantidad) return res.status(400).json({ error: "Inventario insuficiente" });
    
    // Si hay stock, lo descontamos
    producto.stock -= cantidad;
    res.status(200).json({ mensaje: "Stock descontado exitosamente", stockRestante: producto.stock });
});

app.listen(3001, () => console.log('Inventario Service corriendo en puerto 3001'));