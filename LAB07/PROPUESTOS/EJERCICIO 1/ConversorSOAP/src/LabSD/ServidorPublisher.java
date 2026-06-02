package LabSD;

import javax.xml.ws.Endpoint;

public class ServidorPublisher {
    public static void main(String[] args) {
        System.out.println("Iniciando el Servidor del Laboratorio...");
        
        // Publicamos el servicio en el puerto 1516
        Endpoint.publish("http://localhost:1516/WS/Conversor", new ConversorSOAP());
        
        System.out.println("=================================================");
        System.out.println("¡SERVIDOR ACTIVO EXITOSAMENTE DESDE EL PUBLISHER!");
        System.out.println("URL del WSDL: http://localhost:1516/WS/Conversor?wsdl");
        System.out.println("=================================================");
    }
}