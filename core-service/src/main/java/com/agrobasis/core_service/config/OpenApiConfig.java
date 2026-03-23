package com.agrobasis.core_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI agrobasisOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AgroBasis Core API")
                        .description("Documentação oficial do ecossistema Core do AgroBasis. Contratos de integração para gestão agrícola.")
                        .version("v0.0.1")
                        .contact(new Contact()
                                .name("Equipe de Engenharia AgroBasis")
                                .email("eng@agrobasis.com"))
                        .license(new License()
                                .name("Proprietário")
                                .url("https://agrobasis.com/license")));
    }
}
