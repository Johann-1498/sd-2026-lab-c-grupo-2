import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;

public class CalculatorImpl extends UnicastRemoteObject implements Calculator {

    public CalculatorImpl() throws RemoteException {
        super();
    }

    @Override
    public double add(double a, double b) throws RemoteException { return a + b; }
    
    @Override
    public double subtract(double a, double b) throws RemoteException { return a - b; }

    @Override
    public double multiply(double a, double b) throws RemoteException { return a * b; }

    @Override
    public double divide(double a, double b) throws RemoteException {
        if (b == 0) throw new ArithmeticException("Error: División por 0");
        return a / b;
    }

    @Override
    public double power(double a, double b) throws RemoteException { return Math.pow(a, b); }
}