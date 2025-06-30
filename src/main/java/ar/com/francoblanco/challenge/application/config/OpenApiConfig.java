package ar.com.francoblanco.challenge.application.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@Configuration
@OpenAPIDefinition(
		info = @Info(
				title = "Java Challenge",
				version = "1.0.0",
				description = "App para el control de pedidos para un Java Challenge"
				)
		)
public class OpenApiConfig {

}
