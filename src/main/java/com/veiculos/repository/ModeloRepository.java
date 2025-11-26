package com.veiculos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.veiculos.entity.Modelo;

@Repository
public interface ModeloRepository extends JpaRepository<Modelo, Long> {

    boolean existsByNome(String nome);

    // Remove registros pelo nome e retorna a quantidade excluá­da
    long deleteByNome(String nome);
    
    // Verifica se existem veá­culos associados ao modelo
    @Query("SELECT CASE WHEN COUNT(v) > 0 THEN true ELSE false END FROM Veiculo v WHERE v.modelo.id = :modeloId")
    boolean temVeiculosAssociados(@Param("modeloId") Long modeloId);
}
