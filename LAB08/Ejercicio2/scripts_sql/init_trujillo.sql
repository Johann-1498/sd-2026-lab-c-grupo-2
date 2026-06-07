CREATE TABLE cuenta (
    id SERIAL PRIMARY KEY,
    cliente VARCHAR(100) NOT NULL,
    saldo NUMERIC(10, 2) NOT NULL CHECK (saldo >= 0)
);

INSERT INTO cuenta (cliente, saldo) VALUES ('Cliente Corporativo', 10000.00);