package com.veiculos.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.veiculos.dto.ModeloDTO;
import com.veiculos.entity.Modelo;
import com.veiculos.exception.custom.BusinessRuleException;
import com.veiculos.exception.custom.ResourceAlreadyExistsException;
import com.veiculos.exception.custom.ResourceNotFoundException;
import com.veiculos.exception.custom.ValidationException;
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
                .orElseThrow(() -> new ResourceNotFoundException("Modelo", id));
    }

    @Transactional
    public ModeloDTO criar(ModeloDTO dto) {
        if (dto.getId() != null) {
            throw new ValidationException("id", "Novo modelo nao deve ter ID");
        }
        if (repository.existsByNome(dto.getNome())) {
            throw new ResourceAlreadyExistsException("Modelo", "nome", dto.getNome());
        }
        if (dto.getFabricante() == null || dto.getFabricante().getId() == null) {
            throw new ValidationException("fabricante", "Fabricante invalido");
        }
        if (fabricanteRepository.findById(dto.getFabricante().getId()).isEmpty()) {
            throw new ResourceNotFoundException("Fabricante", dto.getFabricante().getId());
        }
        //verifica/valida se o id corresponde ao nome do fabricante
        if (!fabricanteRepository.findById(dto.getFabricante().getId()).get().getNome().equals(dto.getFabricante().getNome())) {
            throw new ValidationException("fabricante", "Identificador de fabricante nao corresponde");
        }

        Modelo salvo = repository.save(ModeloMapper.toEntity(dto));
        return ModeloMapper.toDTO(salvo);
    }

    @Transactional
    public ModeloDTO atualizar(Long id, ModeloDTO dto) {
        Modelo existente = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Modelo", id));
        
        // Verifica se o nome ja existe em outro modelo
        if (!existente.getNome().equals(dto.getNome()) && repository.existsByNome(dto.getNome())) {
            throw new ResourceAlreadyExistsException("Modelo", "nome", dto.getNome());
        }
        
        // Valida fabricante
        if (dto.getFabricante() == null || dto.getFabricante().getId() == null) {
            throw new ValidationException("fabricante", "Fabricante invalido");
        }
        if (fabricanteRepository.findById(dto.getFabricante().getId()).isEmpty()) {
            throw new ResourceNotFoundException("Fabricante", dto.getFabricante().getId());
        }
        if (!fabricanteRepository.findById(dto.getFabricante().getId()).get().getNome().equals(dto.getFabricante().getNome())) {
            throw new ValidationException("fabricante", "Identificador de fabricante nao corresponde");
        }
        
        existente.setNome(dto.getNome());
        existente.setFabricante(fabricanteRepository.findById(dto.getFabricante().getId()).get());
        
        Modelo atualizado = repository.save(existente);
        return ModeloMapper.toDTO(atualizado);
    }

    @Transactional
    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Modelo", id);
        }
        
        // Verificar se existem veiculos associados a este modelo
        if (repository.temVeiculosAssociados(id)) {
            throw BusinessRuleException.cannotDeleteWithDependents("Modelo", "veiculos");
        }
        
        repository.deleteById(id);
    }
}