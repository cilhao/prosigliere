package dev.ercilio.prosigliere.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI blogOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Prosigliere Blog API")
                        .description("REST API for managing blog posts and comments")
                        .version("1.0.0")
                        .contact(new Contact().name("Prosigliere")
                                .url("https://github.com/Prosigliere/coding-challenges/blob/main/backend.md")));
    }
}
