package com.github.gasfgrv.dynamodbtest.commons.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI openAPI() {
        log.info("Setting the API server info");
        var server = new Server();
        server.setUrl("http://localhost:8080");
        server.setDescription("Server URL in the development environment");

        log.info("Setting the API developer contact");
        var contact = new Contact();
        contact.setEmail("gustavo_almeida11@hotmail.com");
        contact.setName("Gustavo Silva");
        contact.setUrl("https://github.com/gasfgrv");

        log.info("Setting the API info");
        var info = new Info()
                .title("Music API")
                .version("1.0")
                .contact(contact)
                .description("This API serves as a test for the AWS SDK for DynamoDB functions (query, scan, load and save).");

        log.info("Getting Open API Schema");
        return new OpenAPI()
                .info(info)
                .servers(List.of(server));
    }

}
