package org.itson.APIGateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class GatewayCorsConfig {

    private static final Logger log = LoggerFactory.getLogger(GatewayCorsConfig.class);

    @PostConstruct
    public void init() {
        log.info(">>>>>>>>>>>>>>>> GatewayCorsConfig CARGADA CORRECTAMENTE! <<<<<<<<<<<<<<<<<");
        log.info(">>> NOTA: La configuración CORS ahora se maneja desde application.yml / Config Server");
    }
}
