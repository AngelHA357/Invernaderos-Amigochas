package org.itson.Alarmas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
@EnableDiscoveryClient
public class AlarmasApplication {

	public static void main(String[] args) {
		//Inicializa el programa
		ApplicationContext context = SpringApplication.run(AlarmasApplication.class, args);
	}

}
