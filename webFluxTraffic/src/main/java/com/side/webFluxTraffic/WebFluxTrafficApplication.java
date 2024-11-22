package com.side.webFluxTraffic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class WebFluxTrafficApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebFluxTrafficApplication.class, args);
	}

}
