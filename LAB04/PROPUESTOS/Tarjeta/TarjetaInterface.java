package Tarjeta;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface TarjetaInterface extends Remote {
    // Devuelve los datos del titular
    String obtenerDatos() throws RemoteException;
    
    // Devuelve cuánto saldo disponible tiene la tarjeta
    double consultarSaldo() throws RemoteException;
    
    // Realiza una compra (resta saldo). Devuelve un mensaje de éxito o error
    String realizarCompra(double monto) throws RemoteException;
    
    // Paga la tarjeta (suma saldo). Devuelve un mensaje de confirmación
    String pagarTarjeta(double monto) throws RemoteException;
}