package com.veiculos.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.veiculos.entity.Veiculo;

@Repository
public interface VeiculoRepository extends JpaRepository<Veiculo, Long> {
    boolean existsByPlaca(String placa);

    Optional<Veiculo> findByPlaca(String placa);

    List<Veiculo> findByPlacaContainingIgnoreCase(String placa);
}
