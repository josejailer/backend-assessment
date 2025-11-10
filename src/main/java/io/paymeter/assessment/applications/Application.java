package io.paymeter.assessment.applications;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@SpringBootApplication
@ComponentScan(basePackages = "io.paymeter.assessment")
@EnableR2dbcRepositories(basePackages = "io.paymeter.assessment.infrastructure.drivenadapters.mysqladapter.entities")
@EntityScan(basePackages = "io.paymeter.assessment.infrastructure.drivenadapters.mysqladapter.entities")
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
