package com.veiculos.service;

import java.time.Year;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.veiculos.dto.VeiculoDTO;
import com.veiculos.entity.Veiculo;
import com.veiculos.mapper.VeiculoMapper;
import com.veiculos.repository.ModeloRepository;
import com.veiculos.repository.VeiculoRepository;

@Service
public class VeiculoService {

    @Autowired
    private VeiculoRepository repository;

    @Autowired
    private ModeloRepository modeloRepository;

    @Transactional(readOnly = true)
    public List<VeiculoDTO> listar() {
        return VeiculoMapper.toDTOList(repository.findAll());
    }

    @Transactional(readOnly = true)
    public VeiculoDTO buscarPorId(Long id) {
        return repository.findById(id)
                .map(VeiculoMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Veículo não encontrado"));
    }

    @Transactional
    public VeiculoDTO criar(VeiculoDTO dto) {
        if (dto.getId() != null) {
            throw new IllegalArgumentException("Novo veículo não deve ter ID");
        }
        if (repository.existsByPlaca(dto.getPlaca())) {
            throw new IllegalArgumentException("Já existe veículo com essa placa");
        }
        if (dto.getModelo() == null || dto.getModelo().getId() == null) {
            throw new IllegalArgumentException("Modelo inválido");
        }
        if (modeloRepository.findById(dto.getModelo().getId()).isEmpty()) {
            throw new IllegalArgumentException("Modelo não encontrado");
        }
        if (dto.getAno() == null || dto.getAno() < 1886 || dto.getAno() > Year.now().getValue() + 1) {
            throw new IllegalArgumentException("Ano inválido");
        }
        Veiculo salvo = repository.save(VeiculoMapper.toEntity(dto));
        return VeiculoMapper.toDTO(salvo);
    }

    @Transactional
    public VeiculoDTO atualizar(Long id, VeiculoDTO dto) {
        Veiculo existente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Veículo não encontrado"));
        if (dto.getPlaca() != null && !dto.getPlaca().equals(existente.getPlaca())) {
            if (repository.existsByPlaca(dto.getPlaca())) {
                throw new IllegalArgumentException("Já existe veículo com essa placa");
            }
            existente.setPlaca(dto.getPlaca());
        }
        if (dto.getCor() != null) existente.setCor(dto.getCor());
        if (dto.getValor() != null) existente.setValor(dto.getValor());
        if (dto.getAno() != null) existente.setAno(dto.getAno());
        if (dto.getDescricao() != null) existente.setDescricao(dto.getDescricao());
        if (dto.getModelo() != null) existente.setModelo(dto.getModelo());
        return VeiculoMapper.toDTO(repository.save(existente));
    }

    @Transactional
    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Veículo não encontrado");
        }
        repository.deleteById(id);
    }
}
