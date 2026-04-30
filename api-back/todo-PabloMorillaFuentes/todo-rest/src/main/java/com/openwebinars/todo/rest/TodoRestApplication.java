package com.openwebinars.todo.rest;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@OpenAPIDefinition(info =
@Info(description = "Una API REST sobre tareas",
		version = "1.0",
		contact = @Contact(email = "pablo.morilla@example.com", name = "Pablo Morilla"),
		license = @License(name = "CC BY"),
		title = "API sobre tareas"
)
)
@SpringBootApplication
@EnableJpaAuditing
public class TodoRestApplication {

	public static void main(String[] args) {
		SpringApplication.run(TodoRestApplication.class, args);
	}

}
