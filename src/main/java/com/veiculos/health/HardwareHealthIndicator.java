package com.veiculos.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;

import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import com.veiculos.util.ByteConverter;

@Component
public class HardwareHealthIndicator implements HealthIndicator {
    @Override
    public Health health() {
    // Bean é um objeto gerenciado pelo Spring ou pela JVM que representa um recurso ou serviço reutilizável,
    // como acesso a informações do sistema operacional, memória, ou outros componentes do ambiente de execução.
    // Ao usar @Component, você está criando um bean gerenciado pelo Spring.
    // O Spring detecta a classe anotada, instancia e gerencia seu ciclo de vida, permitindo injeção automática (@Autowired) em outros lugares do projeto.
    // Ou seja: toda classe anotada com @Component (ou @Service, @Repository, @Controller e @RestController) vira um bean do Spring.

    // Bean para informações do sistema operacional
    OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
    // Bean para informações de tempo de execução da JVM
    RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
    // Número de processadores disponíveis para a JVM
    int availableProcessors = osBean.getAvailableProcessors();
    // Carga média do sistema nos últimos minutos
    double systemLoad = osBean.getSystemLoadAverage();
    // Tempo de atividade da JVM em milissegundos
    long uptime = runtimeBean.getUptime();
    // Nome do sistema operacional
    String osName = osBean.getName();
    // Arquitetura do sistema operacional
    String arch = osBean.getArch();
    // Versão do sistema operacional
    String version = osBean.getVersion();

    // Memória RAM
    // Bean para informações de memória da JVM
    MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
    // Uso de memória heap (área principal de alocação de objetos)
    MemoryUsage heap = memoryBean.getHeapMemoryUsage();
    // Uso de memória non-heap (metadados, código compilado, etc.)
    MemoryUsage nonHeap = memoryBean.getNonHeapMemoryUsage();
    // Soma máxima de heap e non-heap (total disponível para JVM)
    long totalMemory = heap.getMax() + nonHeap.getMax();
    // Soma usada de heap e non-heap (memória efetivamente em uso)
    long usedMemory = heap.getUsed() + nonHeap.getUsed();
    // Memória livre calculada
    long freeMemory = totalMemory - usedMemory;

    return Health.up()
        .withDetail("Sistema Operacional", osName)
        .withDetail("Arquitetura", arch)
        .withDetail("Versão SO", version)
        .withDetail("Processadores Disponíveis", availableProcessors)
        .withDetail("Carga Média do Sistema", systemLoad)
        .withDetail("Uptime (ms)", uptime)
        .withDetail("Memória Total", ByteConverter.humanReadable(totalMemory))
        .withDetail("Memória Usada", ByteConverter.humanReadable(usedMemory))
        .withDetail("Memória Livre", ByteConverter.humanReadable(freeMemory))
        .withDetail("Origem", "API")
        .build();
    }
}
