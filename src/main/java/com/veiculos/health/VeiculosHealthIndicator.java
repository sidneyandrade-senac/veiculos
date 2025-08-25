package com.veiculos.health;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import com.veiculos.repository.FabricanteRepository;
import org.springframework.stereotype.Component;

@Component
public class VeiculosHealthIndicator implements HealthIndicator {

    @Autowired
    private FabricanteRepository fabricanteRepository;

    @Override
    public Health health() {
        try {
            // Usa JPA para testar acesso ao banco (conta fabricantes)
            long count = fabricanteRepository.count();
        return Health.up()
            .withDetail("Aplica??o", "Ve?culos API est? saud?vel")
            .withDetail("Vers?o", "1.0.0")
            .withDetail("Quantidade de Fabricantes", count)
            .withDetail("Banco de Dados", "OK")
            .withDetail("Origem", "API")
            .build();
        } catch (Exception e) {
        return Health.down(e)
            .withDetail("Aplica??o", "Erro ao acessar o banco de dados")
            .withDetail("Origem", "API")
            .build();
        }
    }
}
