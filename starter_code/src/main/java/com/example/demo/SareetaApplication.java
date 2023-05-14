package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@EnableJpaRepositories("com.example.demo.model.persistence.repositories")
@EntityScan("com.example.demo.model.persistence")
@SpringBootApplication
public class SareetaApplication {

	private static final Logger logger = LoggerFactory.getLogger(SareetaApplication.class);
	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder(){
		logger.info("Loaded " + BCryptPasswordEncoder.class.getName());
		return new BCryptPasswordEncoder();
	}
	public static void main(String[] args) {
		logger.info("SareetaApplication running");
		SpringApplication.run(SareetaApplication.class, args);
	}

}
