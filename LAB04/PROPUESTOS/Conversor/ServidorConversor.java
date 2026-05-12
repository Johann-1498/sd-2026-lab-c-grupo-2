package Conversor;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class ServidorConversor {
    public static void main(String[] args) {
        System.out.println("Iniciando Servidor de Conversión de Moneda...");
        try {
            // 1. Iniciar el RMI Registry en el puerto por defecto (1099).
            LocateRegistry.createRegistry(1099);
            System.out.println("RMI Registry iniciado en puerto 1099.");

            // 2. Crear el objeto remoto.
            ConversorMonedaImpl conversor = new ConversorMonedaImpl();

            // 3. Registrar (hacer público) el objeto remoto con un nombre lógico.
            Naming.rebind("rmi://localhost/ConversorMoneda", conversor);
            System.out.println("Servidor listo. Objeto 'ConversorMoneda' registrado.");
        } catch (Exception e) {
            System.out.println("Error al iniciar el servidor: " + e.toString());
            e.printStackTrace();
        }
    }
}