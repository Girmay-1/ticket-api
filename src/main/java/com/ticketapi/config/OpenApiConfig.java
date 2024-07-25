package com.ticketapi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI ticketApiOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Ticket API")
                        .description("A robust, scalable backend service for managing event ticketing")
                        .version("1.0")
                        .license(new License().name("MIT License").url("https://opensource.org/licenses/MIT")))
                .schemaRequirement("jwt_auth", new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT"));
    }
}