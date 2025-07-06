package com.mdw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = {"dao", "interfaces"})
@ComponentScan(basePackages = {"com.mdw","com.mdw.web","dao", "Servicio", "interfaces", "Security"})

public class PryWebMvnDataApplication {

	public static void main(String[] args) {
		SpringApplication.run(PryWebMvnDataApplication.class, args);
	}

}
