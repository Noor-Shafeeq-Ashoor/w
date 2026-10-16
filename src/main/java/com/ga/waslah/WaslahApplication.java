package com.ga.waslah;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class WaslahApplication {

	public static void main(String[] args) {
		SpringApplication.run(WaslahApplication.class, args);
	}

}
