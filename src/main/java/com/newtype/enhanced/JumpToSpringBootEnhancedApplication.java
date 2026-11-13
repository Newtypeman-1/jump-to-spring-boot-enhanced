package com.newtype.enhanced;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class JumpToSpringBootEnhancedApplication {

	public static void main(String[] args) {
		SpringApplication.run(JumpToSpringBootEnhancedApplication.class, args);
	}

}
