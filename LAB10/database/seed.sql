DO $$
BEGIN
    RAISE NOTICE ' Insertando datos iniciales en %', current_database();
END $$;

-- Insertar inventarios iniciales
INSERT INTO inventarios (producto, cantidad, sede) VALUES
('Fresas', 150, 'Lima'),
('Arándanos', 200, 'Lima'),
('Mangos', 80, 'Lima'),
('Palta Hass', 120, 'Lima'),
('Pescado Congelado', 300, 'Lima'),
('Camarones', 100, 'Lima'),
('Lechuga Hidropónica', 250, 'Lima'),
('Frambuesas', 90, 'Lima'),
('Espárragos', 180, 'Lima'),
('Uvas Red Globe', 160, 'Lima');

-- Insertar pedidos iniciales
INSERT INTO pedidos (cliente, producto, cantidad, estado, sede) VALUES
('Supermercado Plaza Vea', 'Fresas', 50, 'En proceso', 'Lima'),
('Restaurante Central', 'Palta Hass', 30, 'Entregado', 'Lima'),
('Exportadora Andina', 'Arándanos', 100, 'Pendiente', 'Lima'),
('Mercado Mayorista', 'Mangos', 40, 'En proceso', 'Lima'),
('Distribuidora Delicatessen', 'Camarones', 25, 'Entregado', 'Lima'),
('Hotel Marriott', 'Frambuesas', 15, 'Pendiente', 'Lima'),
('Cadena de Supermercados Tottus', 'Uvas Red Globe', 60, 'En proceso', 'Lima'),
('Restaurante La Mar', 'Pescado Congelado', 45, 'Entregado', 'Lima'),
('Exportadora Frutícola', 'Espárragos', 80, 'Pendiente', 'Lima'),
('Comida Saludable SAC', 'Lechuga Hidropónica', 120, 'En proceso', 'Lima');

-- Insertar temperaturas de almacenes
INSERT INTO temperaturas (almacen, temperatura, sede) VALUES
('Almacén Norte - Lima', 22.5, 'Lima'),
('Almacén Sur - Lima', 23.0, 'Lima'),
('Almacén Este - Lima', 21.8, 'Lima'),
('Almacén Oeste - Lima', 22.2, 'Lima');

-- Insertar envíos
INSERT INTO envios (pedido_id, estado, ubicacion, sede) VALUES
(1, 'En tránsito', 'Carretera Panamericana - Km 45', 'Lima'),
(2, 'Entregado', 'Miraflores - Tienda Principal', 'Lima'),
(3, 'En preparación', 'Centro de Distribución - Callao', 'Lima'),
(4, 'En tránsito', 'Avenida Javier Prado - Este', 'Lima'),
(5, 'Entregado', 'San Isidro - Oficina Central', 'Lima'),
(6, 'Pendiente', 'Almacén - Zona Norte', 'Lima'),
(7, 'En tránsito', 'Carretera a Chorrillos', 'Lima'),
(8, 'Entregado', 'Barranco - Restaurante', 'Lima'),
(9, 'En preparación', 'Centro de Empaque - Lima', 'Lima'),
(10, 'En tránsito', 'Avenida La Marina - Este', 'Lima');

-- Insertar vehículos
INSERT INTO vehiculos (placa, latitud, longitud, sede) VALUES
('FED-101', -12.043333, -77.028333, 'Lima'),
('FED-102', -12.056944, -77.045000, 'Lima'),
('FED-103', -12.038889, -77.032778, 'Lima'),
('FED-104', -12.066667, -77.033333, 'Lima'),
('FED-105', -12.073333, -77.023333, 'Lima'),
('FED-106', -12.083333, -77.043333, 'Lima');

DO $$
BEGIN
    RAISE NOTICE ' Datos iniciales insertados correctamente';
END $$;