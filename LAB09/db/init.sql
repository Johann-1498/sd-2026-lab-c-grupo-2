-- Create Tables

CREATE TABLE "Inventario" (
    "id" SERIAL NOT NULL,
    "nombre" TEXT NOT NULL,
    "stock" INTEGER NOT NULL,

    CONSTRAINT "Inventario_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "Factura" (
    "id" SERIAL NOT NULL,
    "pedidoId" INTEGER NOT NULL,
    "total" DOUBLE PRECISION NOT NULL,

    CONSTRAINT "Factura_pkey" PRIMARY KEY ("id")
);
CREATE UNIQUE INDEX "Factura_pedidoId_key" ON "Factura"("pedidoId");

CREATE TABLE "Pedido" (
    "id" SERIAL NOT NULL,
    "usuarioId" INTEGER NOT NULL,
    "productoId" INTEGER NOT NULL,
    "cantidad" INTEGER NOT NULL,
    "totalCobrado" DOUBLE PRECISION NOT NULL,
    "estado" TEXT NOT NULL,
    "transporte" TEXT,

    CONSTRAINT "Pedido_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "Envio" (
    "id" SERIAL NOT NULL,
    "pedidoId" INTEGER NOT NULL,
    "usuarioId" INTEGER NOT NULL,
    "estado" TEXT NOT NULL,

    CONSTRAINT "Envio_pkey" PRIMARY KEY ("id")
);

-- Seed Data
INSERT INTO "Inventario" ("id", "nombre", "stock") VALUES (1, 'Manzanas', 100);
INSERT INTO "Inventario" ("id", "nombre", "stock") VALUES (2, 'Leche', 50);

-- Adjust sequence
SELECT setval(pg_get_serial_sequence('"Inventario"', 'id'), coalesce(max(id), 1), max(id) IS NOT null) FROM "Inventario";
