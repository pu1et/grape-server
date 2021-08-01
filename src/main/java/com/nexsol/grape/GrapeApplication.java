package com.nexsol.grape;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GrapeApplication {

	static{
		System.setProperty("spring.config.location", "classpath:/application.yml,classpath:/ncp.yml");
	}

	public static void main(String[] args) {
		SpringApplication.run(GrapeApplication.class, args);
	}

}
