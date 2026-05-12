package Conversor;

import java.rmi.Naming;
import java.util.Scanner;

public class ClienteConversor {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try {
            // 1. Buscar el objeto remoto en el registro.
            System.out.println("Buscando el servicio de conversión...");
            ConversorMonedaInterface conversor = (ConversorMonedaInterface) Naming.lookup("rmi://localhost/ConversorMoneda");
            System.out.println("¡Servicio encontrado!");

            System.out.print("Ingrese el monto en Soles (PEN): ");
            double soles = scanner.nextDouble();

            // 2. Llamar a los métodos remotos como si fueran locales.
            double dolares = conversor.convertirADolares(soles);
            double euros = conversor.convertirAEuros(soles);

            // 3. Mostrar los resultados.
            System.out.println("\n--- Resultados de la Conversión ---");
            System.out.printf("S/ %.2f PEN equivalen a $ %.2f USD%n", soles, dolares);
            System.out.printf("S/ %.2f PEN equivalen a € %.2f EUR%n", soles, euros);

        } catch (Exception e) {
            System.out.println("Error en el cliente: " + e.toString());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
}