package com.veiculos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.veiculos.entity.Modelo;

@Repository
public interface ModeloRepository extends JpaRepository<Modelo, Long> {

    boolean existsByNome(String nome);

    // Remove registros pelo nome e retorna a quantidade excluída
    long deleteByNome(String nome);
}