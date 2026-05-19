package com.unsa.grpc.server;

import java.io.IOException;

import com.unsa.grpc.converter.ConversionType;
import com.unsa.grpc.converter.ConvertRequest;
import com.unsa.grpc.converter.ConvertResponse;
import com.unsa.grpc.converter.ConverterGrpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

public class ConverterServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        int port = 50055;
        Server server = ServerBuilder.forPort(port)
                .addService(new ConverterServiceImpl())
                .build()
                .start();

        System.out.println("==================================================");
        System.out.println("✅ [LOG] Servidor gRPC iniciado en el puerto " + port);
        System.out.println("✅ [LOG] Esperando peticiones de clientes...");
        System.out.println("==================================================\n");
        server.awaitTermination();
    }

    static class ConverterServiceImpl extends ConverterGrpc.ConverterImplBase {
        private static final double TASA_DOLAR = 3.75; 

        @Override
        public void convert(ConvertRequest req, StreamObserver<ConvertResponse> responseObserver) {
            double value = req.getValue();
            ConversionType type = req.getType();
            double result = 0.0;
            boolean success = true;
            String message = "Conversión exitosa.";

            System.out.println("📥 [REQUEST] Se recibió -> Valor: " + value + " | Tipo: " + type.name());

            // Validación de Entradas
            if ((type == ConversionType.SOLES_TO_DOLLARS || type == ConversionType.DOLLARS_TO_SOLES) && value < 0) {
                success = false;
                message = "Error: El monto de dinero no puede ser negativo.";
            } else if ((type == ConversionType.KILOMETERS_TO_MILES || type == ConversionType.MILES_TO_KILOMETERS || type == ConversionType.KILOGRAMS_TO_POUNDS) && value < 0) {
                success = false;
                message = "Error: Las medidas de distancia o peso no pueden ser negativas.";
            } else if (type == ConversionType.CELSIUS_TO_FAHRENHEIT && value < -273.15) {
                success = false;
                message = "Error: La temperatura no puede ser menor al cero absoluto.";
            }

            // Conversión Lógica
            if (success) {
                switch (type) {
                    case CELSIUS_TO_FAHRENHEIT: result = (value * 1.8) + 32; break;
                    case FAHRENHEIT_TO_CELSIUS: result = (value - 32) / 1.8; break;
                    case SOLES_TO_DOLLARS:      result = value / TASA_DOLAR; break;
                    case DOLLARS_TO_SOLES:      result = value * TASA_DOLAR; break;
                    case KILOMETERS_TO_MILES:   result = value * 0.621371; break;
                    case MILES_TO_KILOMETERS:   result = value / 0.621371; break;
                    case KILOGRAMS_TO_POUNDS:   result = value * 2.20462; break;
                    default:
                        success = false;
                        message = "Conversión desconocida.";
                }
            }

            System.out.println("📤 [RESPONSE] Estado: " + (success ? "OK" : "ERROR") + " | Resultado: " + result);
            System.out.println("--------------------------------------------------");

            ConvertResponse response = ConvertResponse.newBuilder()
                    .setSuccess(success)
                    .setResult(result)
                    .setMessage(message)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
}