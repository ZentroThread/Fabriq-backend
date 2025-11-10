package com.example.FabriqBackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class FabriqBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(FabriqBackendApplication.class, args);
	}

}
