package Conversor;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ConversorMonedaImpl extends UnicastRemoteObject implements ConversorMonedaInterface {

    // El constructor debe lanzar RemoteException como lo exige UnicastRemoteObject
    public ConversorMonedaImpl() throws RemoteException {
        super(); // Aunque no es estrictamente necesario, es una buena práctica.
    }

    @Override
    public double convertirADolares(double montoSoles) throws RemoteException {
        System.out.println("Servidor: Recibida solicitud para convertir S/." + montoSoles + " a USD.");
        return montoSoles * TASA_DOLAR;
    }

    @Override
    public double convertirAEuros(double montoSoles) throws RemoteException {
        System.out.println("Servidor: Recibida solicitud para convertir S/." + montoSoles + " a EUR.");
        return montoSoles * TASA_EURO;
    }
}