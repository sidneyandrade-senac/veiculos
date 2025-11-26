package com.veiculos.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

@Component
public class ViaCepHealthIndicator implements HealthIndicator {
    @Override
    public Health health() {
        String url = "https://viacep.com.br/ws/01001000/json/";
        RestTemplate restTemplate = new RestTemplate();
        long start = System.currentTimeMillis();
        try {
            Map<?, ?> resp = restTemplate.getForObject(url, Map.class);
            long elapsed = System.currentTimeMillis() - start;

            // Classificaá§ão por tempo
            String qualidade;
            if (elapsed < 200) {
                qualidade = "á³timo";
            } else if (elapsed < 400) {
                qualidade = "bom";
            } else {
                qualidade = "ruim";
            }

            // Validaá§ão de campos esperados
            Map<String, String> esperado = new HashMap<>();
            esperado.put("cep", "01001-000");
            esperado.put("uf", "SP");
            esperado.put("localidade", "S?o Paulo");

            List<String> diferencas = new ArrayList<>();
            for (Map.Entry<String, String> e : esperado.entrySet()) {
                Object v = (resp != null) ? resp.get(e.getKey()) : null;
                String obtido = (v == null) ? null : v.toString();
                if (!e.getValue().equals(obtido)) {
                    diferencas.add(e.getKey() + ": esperado=" + e.getValue() + ", obtido=" + obtido);
                }
            }

            boolean validacaoOk = diferencas.isEmpty();
            Health.Builder builder = validacaoOk ? Health.up() : Health.status("DEGRADED");

            return builder
                    .withDetail("API Externa", url)
                    .withDetail("Tempo de Resposta (ms)", elapsed)
                    .withDetail("Classificaá§ão", qualidade)
                    .withDetail("Validaá§ão", validacaoOk ? "OK" : "Falhou")
                    .withDetail("CEP", resp != null ? resp.get("cep") : null)
                    .withDetail("Localidade", resp != null ? resp.get("localidade") : null)
                    .withDetail("UF", resp != null ? resp.get("uf") : null)
                    .withDetail("Diferená§as", validacaoOk ? "Nenhuma" : String.join("; ", diferencas))
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
