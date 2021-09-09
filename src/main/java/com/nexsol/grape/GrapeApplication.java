package com.nexsol.grape;

import com.nexsol.grape.util.ImportUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@SpringBootApplication
public class GrapeApplication {

	static{
		System.setProperty("spring.config.location", "classpath:/application.yml,classpath:/ncp.yml,classpath:/import.yml");
	}

	public static void main(String[] args) {

		SpringApplication.run(GrapeApplication.class, args);
	}

}
