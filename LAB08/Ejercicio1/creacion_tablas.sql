-- Script de inicialización para Nodo Arequipa
CREATE DATABASE almacen_arequipa;
\c almacen_arequipa
CREATE TABLE inventario(
    id SERIAL PRIMARY KEY,
    producto VARCHAR(100),
    stock INTEGER
);
INSERT INTO inventario(producto, stock) VALUES('Paracetamol', 100);

-- Script de inicialización para Nodo Lima
CREATE DATABASE almacen_lima;
\c almacen_lima
CREATE TABLE inventario(
    id SERIAL PRIMARY KEY,
    producto VARCHAR(100),
    stock INTEGER
);
INSERT INTO inventario(producto, stock) VALUES('Paracetamol', 50);