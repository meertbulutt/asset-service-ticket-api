package com.example.assetserviceticket.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI assetServiceTicketOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Asset Service Ticket API")
                        .description("A backend REST API for managing organizational assets and service tickets.")
                        .version("v1"));
    }
}
