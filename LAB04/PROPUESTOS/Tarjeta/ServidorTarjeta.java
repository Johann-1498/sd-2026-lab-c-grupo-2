package Tarjeta;

import java.rmi.Naming;

public class ServidorTarjeta {
    public static void main(String[] args) {
        try {
            // Creamos una tarjeta con un límite de 5000 soles
            TarjetaImpl miTarjeta = new TarjetaImpl("4555-1234-9876-0000", "Juan Perez", 5000.0);
            
            // Publicamos el objeto remoto en el puerto 1099
            Naming.rebind("rmi://localhost:1099/ServicioTarjeta", miTarjeta);
            
            System.out.println("Servidor RMI de Tarjetas de Crédito listo y en ejecución...");
            
        } catch (Exception e) {
            System.out.println("Ocurrió un error en el servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
}