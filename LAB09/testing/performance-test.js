import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    vus: 100, 
    duration: '5m', 
};

export default function () {
    // 1. Prueba de creación de Pedido
    const urlPedido = 'http://localhost:3006/api/pedidos';
    const payload = JSON.stringify({
        usuarioId: __VU,
        productoId: 1, 
        cantidad: 1,
        precioBase: 100
    });
    const params = { headers: { 'Content-Type': 'application/json' } };
    const resPedido = http.post(urlPedido, payload, params);

    check(resPedido, {
        'Pedido status es 201': (r) => r.status === 201,
        'Pedido tiempo < 8000ms': (r) => r.timings.duration < 8000,
    });

    // 2. Prueba de lectura de Promociones (Facturación)
    const urlFacturacion = 'http://localhost:3003/api/promociones/VERANO20';
    const resFact = http.get(urlFacturacion);

    check(resFact, {
        'Facturación status es 200': (r) => r.status === 200,
        'Facturación tiempo < 2000ms': (r) => r.timings.duration < 2000,
    });

    sleep(1);
}
