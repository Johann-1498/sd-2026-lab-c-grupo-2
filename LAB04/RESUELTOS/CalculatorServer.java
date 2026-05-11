package LAB04.RESUELTOS;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class CalculatorServer {

    public CalculatorServer() {

        try {

            LocateRegistry.createRegistry(1099);
            System.out.println("RMI Registry iniciado");
            Calculator c = new CalculatorImpl();
            Naming.rebind(
                    "rmi://localhost/CalculatorService",
                    c
            );

            System.out.println("Servidor listo");
        }

        catch (Exception e) {
            System.out.println("Trouble: " + e);
        }
    }

    public static void main(String args[]) {
        new CalculatorServer();
    }
}