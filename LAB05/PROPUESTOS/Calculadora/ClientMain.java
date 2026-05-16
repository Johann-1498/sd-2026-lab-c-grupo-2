import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ClientMain {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            
            Calculator calc = (Calculator) registry.lookup("CalculadoraRMI");
            
            double num1 = 5.0;
            double num2 = 2.0;
            
            System.out.println("Enviando números al servidor: " + num1 + " y " + num2);
            
            System.out.println("Multiplicación: " + calc.multiply(num1, num2));
            System.out.println("División: " + calc.divide(num1, num2));
            System.out.println("Potencia: " + calc.power(num1, num2));
            
        } catch (Exception e) {
            System.err.println("Error en el cliente: " + e.toString());
            e.printStackTrace();
        }
    }
}