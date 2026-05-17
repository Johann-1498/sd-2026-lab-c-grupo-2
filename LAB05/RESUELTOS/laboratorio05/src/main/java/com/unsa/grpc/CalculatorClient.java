package com.unsa.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class CalculatorClient {
    public static void main(String[] args) {
        // Abrimos el canal de comunicación con el servidor local en texto plano (sin SSL)
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        // Creamos el conector (stub) síncrono
        CalculatorGrpc.CalculatorBlockingStub stub = CalculatorGrpc.newBlockingStub(channel);

        // Preparamos los datos a enviar (8 y 4 según la guía del docente)
        Request request = Request.newBuilder()
                .setA(8)
                .setB(4)
                .build();

        // Invocamos el método remoto 'sum' de forma transparente
        Response response = stub.sum(request);

        // Imprimimos el resultado final esperado en la consola
        System.out.println("Resultado: " + response.getResult());

        // Cerramos el canal limpiamente
        channel.shutdown();
    }
}