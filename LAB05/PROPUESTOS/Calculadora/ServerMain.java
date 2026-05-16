import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerMain {
    public static void main(String[] args) {
        try {
            Calculator stub = new CalculatorImpl();
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("CalculadoraRMI", stub);
            System.out.println("Servidor RMI listo y esperando conexiones...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}