package org.itson.Autenticacion.proto;

import io.grpc.stub.StreamObserver;
import io.jsonwebtoken.Claims;
import net.devh.boot.grpc.server.service.GrpcService;
import org.itson.Autenticacion.servicio.JwtService;
import org.itson.grpc.autenticacion.AutenticacionServicioGrpc;
import org.itson.grpc.autenticacion.TokenRequest;
import org.itson.grpc.autenticacion.TokenValidationResponse;
import org.springframework.beans.factory.annotation.Autowired;

@GrpcService
public class ServidorAutenticacionGrpc extends AutenticacionServicioGrpc.AutenticacionServicioImplBase {

    @Autowired
    private JwtService jwtService;

    @Override
    public void validarToken(TokenRequest request, StreamObserver<TokenValidationResponse> responseObserver) {
        String token = request.getToken();
        
        TokenValidationResponse.Builder responseBuilder = TokenValidationResponse.newBuilder();
        
        try {
            // Validar el token
            Claims claims = jwtService.validateToken(token);
            
            // Construir respuesta exitosa
            responseBuilder
                .setValido(true)
                .setUserId(claims.getSubject())
                .setRole(claims.get("role", String.class))
                .setMensaje("Token válido");
            
        } catch (Exception e) {
            // Construir respuesta de error
            responseBuilder
                .setValido(false)
                .setMensaje("Token inválido: " + e.getMessage());
        }
        
        // Enviar respuesta
        TokenValidationResponse response = responseBuilder.build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
