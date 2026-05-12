package Tarjeta;

import java.rmi.Naming;
import java.util.Scanner;

public class ClienteTarjeta {
    public static void main(String[] args) {
        try {
            // Buscamos el servicio en el RMI Registry
            TarjetaInterface tarjeta = (TarjetaInterface) Naming.lookup("rmi://localhost/ServicioTarjeta");
            Scanner sc = new Scanner(System.in);
            int opcion = 0;

            System.out.println("==================================");
            System.out.println("Conectado a: " + tarjeta.obtenerDatos());
            System.out.println("==================================");

            while (opcion != 4) {
                System.out.println("\n--- MENÚ DE TARJETA DE CRÉDITO ---");
                System.out.println("1. Consultar Saldo Disponible");
                System.out.println("2. Realizar una Compra");
                System.out.println("3. Pagar Tarjeta");
                System.out.println("4. Salir");
                System.out.print("Seleccione una opción: ");
                
                opcion = sc.nextInt();

                switch (opcion) {
                    case 1:
                        System.out.println("-> Saldo disponible: S/." + tarjeta.consultarSaldo());
                        break;
                    case 2:
                        System.out.print("Ingrese el monto de la compra: S/.");
                        double compra = sc.nextDouble();
                        System.out.println("-> " + tarjeta.realizarCompra(compra));
                        break;
                    case 3:
                        System.out.print("Ingrese el monto a pagar: S/.");
                        double pago = sc.nextDouble();
                        System.out.println("-> " + tarjeta.pagarTarjeta(pago));
                        break;
                    case 4:
                        System.out.println("Saliendo del sistema...");
                        break;
                    default:
                        System.out.println("Opción no válida, intente de nuevo.");
                }
            }
            sc.close();
            
        } catch (Exception e) {
            System.out.println("Error en el cliente: " + e.getMessage());
            e.printStackTrace();
        }
    }
}