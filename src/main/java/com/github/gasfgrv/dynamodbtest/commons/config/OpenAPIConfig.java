package com.github.gasfgrv.dynamodbtest.commons.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI openAPI() {
        var server = new Server();
        server.setUrl("http://localhost:8080");
        server.setDescription("Server URL in Development environment");

        var contact = new Contact();
        contact.setEmail("gustavo_almeida11@hotmail.com");
        contact.setName("Gustavo Silva");
        contact.setUrl("https://github.com/gasfgrv");

        var info = new Info()
                .title("Music API")
                .version("1.0")
                .contact(contact)
                .description("This api serves as a test for the aws sdk for dynamodb functions (query, load and save).");

        return new OpenAPI()
                .info(info)
                .servers(List.of(server));
    }

}
