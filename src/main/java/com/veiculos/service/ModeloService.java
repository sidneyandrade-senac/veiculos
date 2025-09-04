package com.veiculos.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.veiculos.dto.ModeloDTO;
import com.veiculos.entity.Modelo;
import com.veiculos.mapper.ModeloMapper;
import com.veiculos.repository.FabricanteRepository;
import com.veiculos.repository.ModeloRepository;

@Service
public class ModeloService {

    @Autowired
    private ModeloRepository repository;

    @Autowired
    private FabricanteRepository fabricanteRepository;

    @Transactional(readOnly = true)
    public List<ModeloDTO> listar() {
        return ModeloMapper.toDTOList(repository.findAll());
    }

    @Transactional(readOnly = true)
    public ModeloDTO buscarPorId(Long id) {
        return repository.findById(id)
                .map(ModeloMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Modelo não encontrado"));
    }

    @Transactional
    public ModeloDTO criar(ModeloDTO dto) {
        if (dto.getId() != null) {
            throw new IllegalArgumentException("Novo modelo não deve ter ID");
        }
        if (repository.existsByNome(dto.getNome())) {
            throw new IllegalArgumentException("Já existe modelo com esse nome");
        }
        if (dto.getFabricante() == null || dto.getFabricante().getId() == null) {
            throw new IllegalArgumentException("Fabricante inválido");
        }
        if (fabricanteRepository.findById(dto.getFabricante().getId()).isEmpty()) {
            throw new IllegalArgumentException("Fabricante não encontrado");
        }
        //verifica/valida se o id corresponde ao nome do fabricante
        if (!fabricanteRepository.findById(dto.getFabricante().getId()).get().getNome().equals(dto.getFabricante().getNome())) {
            throw new IllegalArgumentException("Identificador de fabricante não corresponde");
        }

        Modelo salvo = repository.save(ModeloMapper.toEntity(dto));
        return ModeloMapper.toDTO(salvo);
    }

    @Transactional
    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Modelo não encontrado");
        }
        repository.deleteById(id);
    }
}