package com.veiculos.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.veiculos.dto.FabricanteDTO;
import com.veiculos.entity.Fabricante;
import com.veiculos.mapper.FabricanteMapper;
import com.veiculos.repository.FabricanteRepository;

@Service
public class FabricanteService {

    private final FabricanteRepository repository;

    public FabricanteService(FabricanteRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<FabricanteDTO> listar() {
        return FabricanteMapper.toDTOList(repository.findAll());
    }

    @Transactional(readOnly = true)
    public FabricanteDTO buscarPorId(Long id) {
        return repository.findById(id)
                .map(FabricanteMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Fabricante não encontrado"));
    }

    @Transactional
    public FabricanteDTO criar(FabricanteDTO dto) {
        if (dto.getId() != null) {
            throw new IllegalArgumentException("Novo fabricante não deve ter ID");
        }
        if (repository.existsByNome(dto.getNome())) {
            throw new IllegalArgumentException("Já existe fabricante com esse nome");
        }
        Fabricante salvo = repository.save(FabricanteMapper.toEntity(dto));
        return FabricanteMapper.toDTO(salvo);
    }

    @Transactional
    public FabricanteDTO atualizar(Long id, FabricanteDTO dto) {
        Fabricante existente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fabricante não encontrado"));
        existente.setNome(dto.getNome());
        existente.setPaisOrigem(dto.getPaisOrigem());
        return FabricanteMapper.toDTO(repository.save(existente));
    }

    @Transactional
    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Fabricante não encontrado");
        }
        repository.deleteById(id);
    }
}
