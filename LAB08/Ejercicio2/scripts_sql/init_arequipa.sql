CREATE TABLE cuenta (
    id SERIAL PRIMARY KEY,
    cliente VARCHAR(100) NOT NULL,
    saldo NUMERIC(10, 2) NOT NULL CHECK (saldo >= 0) -- Check para evitar saldos negativos
);

INSERT INTO cuenta (cliente, saldo) VALUES ('Cliente Corporativo', 50000.00);