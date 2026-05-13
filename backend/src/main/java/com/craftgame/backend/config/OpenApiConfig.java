package com.craftgame.backend.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Random;

@Configuration
@OpenAPIDefinition(
        info = @Info(title = "Craft Game API", version = "v1", description = "Software delivery management serious game API"),
        servers = @Server(url = "http://localhost:8080", description = "Local server")
)
public class OpenApiConfig {

    @Bean
    public Random random() {
        return new Random();
    }
}
