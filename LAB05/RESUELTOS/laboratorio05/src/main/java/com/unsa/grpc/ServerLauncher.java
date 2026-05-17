package com.unsa.grpc;

import java.io.IOException;

import io.grpc.Server;
import io.grpc.ServerBuilder;

public class ServerLauncher {
    public static void main(String[] args) throws IOException, InterruptedException {
        // Configuramos el servidor para escuchar en el puerto 50051
        Server server = ServerBuilder.forPort(50051)
                .addService(new CalculatorServiceImpl())
                .build();

        System.out.println("Servidor gRPC iniciado en el puerto 50051...");
        server.start();
        
        // Bloquea el hilo principal para que el servidor no se cierre inmediatamente
        server.awaitTermination();
    }
}