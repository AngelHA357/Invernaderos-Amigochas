package org.itson.APIGateway.config;

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.itson.grpc.autenticacion.AutenticacionServicioGrpc;
import org.itson.grpc.autenticacion.TokenRequest;
import org.itson.grpc.autenticacion.TokenValidationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationFilter.class);

    @GrpcClient("autenticacion-service")
    private AutenticacionServicioGrpc.AutenticacionServicioBlockingStub autenticacionStub;

    public AuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            if (shouldSkipAuth(exchange)) {
                return chain.filter(exchange);
            }

            if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                log.error("No se encontró el header de Authorization");
                return onError(exchange, "No se encontró el header de Authorization", HttpStatus.UNAUTHORIZED);
            }

            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.error("Header de autorización incorrecto");
                return onError(exchange, "Header de autorización incorrecto", HttpStatus.UNAUTHORIZED);
            }

            String token = authHeader.substring(7);

            try {
                // Llamar al servicio gRPC para validar el token
                TokenRequest request = TokenRequest.newBuilder()
                        .setToken(token)
                        .build();
                
                TokenValidationResponse response = autenticacionStub.validarToken(request);
                
                if (!response.getValido()) {
                    log.error("Token inválido: {}", response.getMensaje());
                    return onError(exchange, "Token inválido", HttpStatus.UNAUTHORIZED);
                }
                
                // Añadir información del usuario a los headers
                exchange = exchange.mutate()
                        .request(exchange.getRequest().mutate()
                                .header("X-User-Id", response.getUserId())
                                .header("X-User-Role", response.getRole())
                                .build())
                        .build();
                
                return chain.filter(exchange);
            } catch (Exception e) {
                log.error("Error validando token: {}", e.getMessage());
                return onError(exchange, "Error validando token", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        });
    }

    private boolean shouldSkipAuth(ServerWebExchange exchange) {
        String path = exchange.getRequest().getURI().getPath();
        // Omitir autenticación para rutas de autenticación y login
        return path.contains("/auth/") || path.contains("/api/v1/login");
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        exchange.getResponse().setStatusCode(httpStatus);
        return exchange.getResponse().setComplete();
    }

    public static class Config {
        // Configuración adicional si se requiere
    }
}
