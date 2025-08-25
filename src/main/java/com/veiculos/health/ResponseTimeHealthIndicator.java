package com.veiculos.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ResponseTimeHealthIndicator implements HealthIndicator {
    @Override
    public Health health() {
        String url = "https://viacep.com.br/ws/01001000/json/";
    // Cria uma inst?ncia de RestTemplate, que ? um cliente HTTP s?ncrono usado para fazer requisi??es REST (GET, POST, etc.) a servi?os externos.
    RestTemplate restTemplate = new RestTemplate();
        long start = System.currentTimeMillis();
        try {
            // Apenas mede tempo de resposta da API (exemplo simples)
            restTemplate.getForObject(url, String.class);
            long elapsed = System.currentTimeMillis() - start;
            String qualidade;
            if (elapsed < 200) {
                qualidade = "?timo";
            } else if (elapsed < 400) {
                qualidade = "bom";
            } else {
                qualidade = "ruim";
            }
            return Health.up()
                .withDetail("API Externa", url)
                .withDetail("Tempo de Resposta (ms)", elapsed)
                .withDetail("Classifica??o", qualidade)
                .withDetail("Status", "API externa OK")
                .withDetail("Origem", "API")
                .build();
        } catch (Exception e) {
            long elapsed = System.currentTimeMillis() - start;
        return Health.down(e)
            .withDetail("API Externa", url)
            .withDetail("Tempo de Resposta (ms)", elapsed)
            .withDetail("Status", "Falha ao acessar API externa")
            .withDetail("Origem", "API")
            .build();
        }
    }
}
