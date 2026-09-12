package com.alexhiz.hexagonal.helpdesk.shared.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    public OpenAPI customOpenApi(){
        return new OpenAPI().info(new Info()
                .title("Inventory Tecnology")
                .version("1.0")
                .description("Sistema de tickets con arquitectura hexagonal.")
                .contact(new Contact()
                        .name("Alexis Jimenez")
                        .email("alexhizdev@gmail.com")));
    }
}
