package br.com.psiconnect.consultorio.infrastructure.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration {

    @Bean
    public OpenAPI psiconnectOpenApi() {
        return new OpenAPI().info(new Info()
                .title("PsiConnect API")
                .version("1.0")
                .description("API para administrar pacientes, psicólogos, locais de atendimento, agenda, presença, evoluções clínicas, altas e relatórios. " +
                        "Os endpoints paginados aceitam page (iniciando em zero), size e sort. Datas e horários seguem o padrão ISO-8601, salvo indicação específica.")
                .license(new License().name("Uso interno")));
    }
}
