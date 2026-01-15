package com.example.kafka.producer.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI kafkaProducerOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Kafka Producer API")
                        .description("Kafka producer with dynamic topic routing and centralized configuration")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Charter Communications")
                                .email("support@charter.com")))
                .servers(List.of(
                        new Server().url("http://localhost:8081").description("Development"),
                        new Server().url("https://api.charter.com").description("Production")
                ));
    }
}
