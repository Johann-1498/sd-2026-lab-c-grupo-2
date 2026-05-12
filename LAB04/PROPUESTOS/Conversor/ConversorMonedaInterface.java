package Conversor;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ConversorMonedaInterface extends Remote {
    // Tasa fija de ejemplo: 1 SOL = 0.27 DOLARES
    double TASA_DOLAR = 0.27;
    // Tasa fija de ejemplo: 1 SOL = 0.25 EUROS
    double TASA_EURO = 0.25;

    public double convertirADolares(double montoSoles) throws RemoteException;
    public double convertirAEuros(double montoSoles) throws RemoteException;
}