package LabSD;

import javax.xml.ws.Endpoint;

public class ServidorVentas {
    public static void main(String[] args) {
        System.out.println("Iniciando el Servidor de Ventas Online...");
        
        // Publicamos en el puerto 1517
        Endpoint.publish("http://localhost:1517/WS/Ventas", new VentasSOAP());
        
        System.out.println("=================================================");
        System.out.println("¡SERVICIO DE VENTAS ACTIVO DESDE EL PUBLISHER!");
        System.out.println("URL del WSDL: http://localhost:1517/WS/Ventas?wsdl");
        System.out.println("=================================================");
    }
}