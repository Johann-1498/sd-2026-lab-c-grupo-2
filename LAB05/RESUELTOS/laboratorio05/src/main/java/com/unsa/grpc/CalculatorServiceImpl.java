package com.unsa.grpc;

import io.grpc.stub.StreamObserver;

public class CalculatorServiceImpl extends CalculatorGrpc.CalculatorImplBase {
    @Override
    public void sum(Request req, StreamObserver<Response> responseObserver) {
        // Obtenemos los parámetros a y b del Request y los sumamos
        int result = req.getA() + req.getB();
        
        // Construimos la respuesta estructurada
        Response response = Response.newBuilder()
                .setResult(result)
                .build();
        
        // Enviamos la respuesta al cliente
        responseObserver.onNext(response);
        // Marcamos que la comunicación terminó con éxito
        responseObserver.onCompleted();
    }
}
