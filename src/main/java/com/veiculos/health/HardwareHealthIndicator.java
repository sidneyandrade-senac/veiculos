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
    // Bean á© um objeto gerenciado pelo Spring ou pela JVM que representa um recurso ou serviá§o reutilizá¡vel,
    // como acesso a informaá§áµes do sistema operacional, memá³ria, ou outros componentes do ambiente de execuá§ão.
    // Ao usar @Component, vocáª está¡ criando um bean gerenciado pelo Spring.
    // O Spring detecta a classe anotada, instancia e gerencia seu ciclo de vida, permitindo injeá§ão automá¡tica (@Autowired) em outros lugares do projeto.
    // Ou seja: toda classe anotada com @Component (ou @Service, @Repository, @Controller e @RestController) vira um bean do Spring.

    // Bean para informaá§áµes do sistema operacional
    OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
    // Bean para informaá§áµes de tempo de execuá§ão da JVM
    RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
    // Náºmero de processadores disponá­veis para a JVM
    int availableProcessors = osBean.getAvailableProcessors();
    // Carga má©dia do sistema nos áºltimos minutos
    double systemLoad = osBean.getSystemLoadAverage();
    // Tempo de atividade da JVM em milissegundos
    long uptime = runtimeBean.getUptime();
    // Nome do sistema operacional
    String osName = osBean.getName();
    // Arquitetura do sistema operacional
    String arch = osBean.getArch();
    // Versão do sistema operacional
    String version = osBean.getVersion();

    // Memá³ria RAM
    // Bean para informaá§áµes de memá³ria da JVM
    MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
    // Uso de memá³ria heap (á¡rea principal de alocaá§ão de objetos)
    MemoryUsage heap = memoryBean.getHeapMemoryUsage();
    // Uso de memá³ria non-heap (metadados, cá³digo compilado, etc.)
    MemoryUsage nonHeap = memoryBean.getNonHeapMemoryUsage();
    // Soma má¡xima de heap e non-heap (total disponá­vel para JVM)
    long totalMemory = heap.getMax() + nonHeap.getMax();
    // Soma usada de heap e non-heap (memá³ria efetivamente em uso)
    long usedMemory = heap.getUsed() + nonHeap.getUsed();
    // Memá³ria livre calculada
    long freeMemory = totalMemory - usedMemory;

    return Health.up()
        .withDetail("Sistema Operacional", osName)
        .withDetail("Arquitetura", arch)
        .withDetail("Versão SO", version)
        .withDetail("Processadores Disponá­veis", availableProcessors)
        .withDetail("Carga Má©dia do Sistema", systemLoad)
        .withDetail("Uptime (ms)", uptime)
        .withDetail("Memá³ria Total", ByteConverter.humanReadable(totalMemory))
        .withDetail("Memá³ria Usada", ByteConverter.humanReadable(usedMemory))
        .withDetail("Memá³ria Livre", ByteConverter.humanReadable(freeMemory))
        .withDetail("Origem", "API")
        .build();
    }
}
