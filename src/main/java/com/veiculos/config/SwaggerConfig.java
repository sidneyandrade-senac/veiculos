
package com.veiculos.config;


// Importa as classes necess?rias do Swagger/OpenAPI
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
// Importa as anota??es do Spring para configura??o e beans
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


// Indica que esta classe ? uma configura??o do Spring
@Configuration
public class SwaggerConfig {



    // URL para acessar a interface Swagger UI ap?s iniciar a aplica??o
    // http://localhost:8080/swagger-ui/index.html

    /**
     * Este m?todo registra uma configura??o personalizada do Swagger/OpenAPI para a aplica??o.
     * O objeto retornado ser? gerenciado pelo Spring e estar? dispon?vel para inje??o em outros componentes.
     * A anota??o @Bean garante que a inst?ncia de OpenAPI seja criada e mantida no contexto da aplica??o.
     */
    // Define um bean do tipo OpenAPI para customizar a documenta??o gerada pelo Swagger
    @Bean
    public OpenAPI customOpenAPI() {
    // Cria uma inst?ncia de OpenAPI e define as informa??es principais da API
    return new OpenAPI()
        .info(
            // Define o t?tulo, vers?o e descri??o da documenta??o
            new Info()
                .title("API de Gestao de Automoveis")
                .version("1.0")
                .description("Documentacao da API para gerenciar Fabricantes, Modelos e Automoveis")
        );
    }
}
