package LAB04.RESUELTOS;

public class CalculatorImpl
        extends java.rmi.server.UnicastRemoteObject
        implements Calculator {

    public CalculatorImpl()
            throws java.rmi.RemoteException {
        super();
    }

    @Override
    public double add(double a, double b)
            throws java.rmi.RemoteException {

        return a + b;
    }

    @Override
    public double sub(double a, double b)
            throws java.rmi.RemoteException {
        return a - b;
    }

    @Override
    public double mul(double a, double b)
            throws java.rmi.RemoteException {
        return a * b;
    }

    @Override
    public double div(double a, double b)
            throws java.rmi.RemoteException {
        if (b == 0) {

            throw new ArithmeticException(
                    "No se puede dividir entre cero"
            );
        }
        return a / b;
    }
}