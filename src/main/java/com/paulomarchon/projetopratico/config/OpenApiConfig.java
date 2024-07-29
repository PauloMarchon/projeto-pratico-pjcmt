package com.paulomarchon.projetopratico.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                title = "Api - Projeto Pratico",
                description = "Documentacao APIs Projeto Pratico",
                version = "1.0.0",
                license = @License(
                        name = "Apache 2.0",
                        url = "http://www.apache.org/licenses/LICENSE-2.0.html"
                ),
                termsOfService = "http://swagger.io/terms/",
                contact = @Contact(
                        name = "Paulo Marchon",
                        email = "example@email.com",
                        url = "https://site-ficticio.com.br"
                )
        ),
        servers = {
                @Server(
                        description = "Ambiente de desenvolvimento",
                        url = "http://localhost:8082"
                ),
                @Server(
                        description = "Ambiente de producao",
                        url = "https://000.000.000.00"
                )
        }
)
public class OpenApiConfig {

}
