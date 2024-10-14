package ms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("APIs User Phone")
                        .version("1.0")
                        .description("Esta API proporciona operaciones relacionadas con usuarios y teléfonos. Incluye autenticación, manejo de usuarios y administración de dispositivos.")
                        .termsOfService("http://example.com/terms")
                        .contact(new Contact()
                                .name("Equipo de Soporte")
                                .email("alejandro.sandoval@ugm.cl")
                                .url("http://support.nissum.com"))
                        .license(new License()
                                .name("Licencia Apache 2.0")
                                .url("http://springdoc.org")));
    }
}
