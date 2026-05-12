package Tarjeta;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;

public class TarjetaImpl extends UnicastRemoteObject implements TarjetaInterface {
    
    private String numeroTarjeta;
    private String titular;
    private double limiteCredito;
    private double saldoDisponible;

    // El constructor obligatorio que lanza RemoteException
    public TarjetaImpl(String numeroTarjeta, String titular, double limiteCredito) throws RemoteException {
        super();
        this.numeroTarjeta = numeroTarjeta;
        this.titular = titular;
        this.limiteCredito = limiteCredito;
        this.saldoDisponible = limiteCredito; // Al inicio, el saldo disponible es el límite total
    }

    @Override
    public String obtenerDatos() throws RemoteException {
        return "Titular: " + titular + " | Tarjeta: " + numeroTarjeta;
    }

    @Override
    public double consultarSaldo() throws RemoteException {
        return saldoDisponible;
    }

    @Override
    public String realizarCompra(double monto) throws RemoteException {
        if (monto <= 0) {
            return "Error: El monto de compra debe ser mayor a cero.";
        }
        if (monto > saldoDisponible) {
            return "Transacción denegada. Saldo insuficiente. Saldo actual: S/." + saldoDisponible;
        }
        saldoDisponible -= monto;
        return "Compra aprobada por S/." + monto + ". Nuevo saldo disponible: S/." + saldoDisponible;
    }

    @Override
    public String pagarTarjeta(double monto) throws RemoteException {
        if (monto <= 0) {
            return "Error: El monto a pagar debe ser mayor a cero.";
        }
        saldoDisponible += monto;
        // Evitamos que el saldo disponible supere el límite de la tarjeta
        if (saldoDisponible > limiteCredito) {
            saldoDisponible = limiteCredito; 
        }
        return "Pago recibido por S/." + monto + ". Nuevo saldo disponible: S/." + saldoDisponible;
    }
}