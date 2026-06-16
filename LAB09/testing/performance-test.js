import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    vus: 100, 
    duration: '5m', 
};

export default function () {
    const url = 'http://localhost:3005/api/pedidos';
    
    const payload = JSON.stringify({
        usuarioId: Math.floor(Math.random() * 1000),
        productoId: 1, 
        cantidad: 1,
        precioBase: 100
    });

    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    const res = http.post(url, payload, params);

    check(res, {
        'status es 201 (Creado)': (r) => r.status === 201,
        'tiempo < 8000ms (Requerimiento)': (r) => r.timings.duration < 8000,
    });

    sleep(0.5);
}
