package com.odinson.registryvehicleapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class RegistryVehicleApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(RegistryVehicleApiApplication.class, args);
	}

}
