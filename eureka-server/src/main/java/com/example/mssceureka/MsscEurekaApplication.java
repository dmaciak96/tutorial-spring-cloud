package com.example.mssceureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class MsscEurekaApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsscEurekaApplication.class, args);
	}

}
