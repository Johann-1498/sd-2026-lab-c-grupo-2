import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerMain {
    public static void main(String[] args) {
        try {
            Calculator stub = new CalculatorImpl();
            
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("CalculadoraRMI", stub);
            
            System.out.println("Servidor RPC Tradicional (RMI) listo y esperando conexiones...");
        } catch (Exception e) {
            System.err.println("Error en el servidor: " + e.toString());
            e.printStackTrace();
        }
    }
}