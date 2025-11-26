package com.veiculos.service;

import java.time.Year;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.transaction.annotation.Transactional;

import com.veiculos.dto.VeiculoDTO;
import com.veiculos.entity.Veiculo;
import com.veiculos.exception.custom.ResourceAlreadyExistsException;
import com.veiculos.exception.custom.ResourceNotFoundException;
import com.veiculos.exception.custom.ValidationException;
import com.veiculos.mapper.VeiculoMapper;
import com.veiculos.repository.ModeloRepository;
import com.veiculos.repository.VeiculoRepository;
import com.veiculos.util.ValidaVeiculo;

@Service
public class VeiculoService {

    @Autowired
    private VeiculoRepository repository;

    @Autowired
    private ModeloRepository modeloRepository;

    @Autowired
    private MessageSource messageSource;

    @Transactional(readOnly = true)
    public Page<VeiculoDTO> listar(Pageable pageable) {
        return repository.findAll(pageable).map(VeiculoMapper::toDTO);
    }

    //lista todos sem pagina??o
    @Transactional(readOnly = true)
    public List<VeiculoDTO> listar() {
        return VeiculoMapper.toDTOList(repository.findAll());
    }

    @Transactional(readOnly = true)
    public VeiculoDTO buscarPorId(Long id) {
        String mensagem = messageSource.getMessage("veiculo.nao.encontrado", null, LocaleContextHolder.getLocale());
        return repository.findById(id)
            .map(VeiculoMapper::toDTO)
            .orElseThrow(() -> new ResourceNotFoundException(mensagem));
    }

    @Transactional(readOnly = true)
    public VeiculoDTO buscarPorPlaca(String placa) {
        if (placa == null || placa.isBlank()) {
            String mensagem = messageSource.getMessage("veiculo.placa.obrigatoria", null, LocaleContextHolder.getLocale());
            throw new ValidationException("placa", mensagem);
        }
        String mensagemNaoEncontrado = messageSource.getMessage("veiculo.nao.encontrado", null, LocaleContextHolder.getLocale());
        return repository.findByPlaca(placa.trim())
                .map(VeiculoMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException(mensagemNaoEncontrado));
    }

    @Transactional(readOnly = true)
    public boolean existePorPlaca(String placa) {
        return repository.existsByPlaca(placa);
    }   

    @Transactional(readOnly = true)
    public List<VeiculoDTO> buscarPorPlacaParcial(String termo) {
        if (termo == null || termo.isBlank()) {
            String mensagem = messageSource.getMessage("veiculo.placa.obrigatoria", null, LocaleContextHolder.getLocale());
            throw new ValidationException("placa", mensagem);
        }
        return VeiculoMapper.toDTOList(repository.findByPlacaContainingIgnoreCase(termo.trim()));
    }

    @Transactional
    public VeiculoDTO criar(VeiculoDTO dto) {
        if (dto.getId() != null) {
            String mensagem = messageSource.getMessage("operacao.falha", new Object[]{"ID presente"}, LocaleContextHolder.getLocale());
            throw new ValidationException("id", mensagem);
        }
        if (!ValidaVeiculo.isPlacaValida(dto)) {
            String mensagem = messageSource.getMessage("erro.formato.placa", new Object[]{dto.getPlaca().toString()}, LocaleContextHolder.getLocale());
            throw new ValidationException("placa", mensagem);
        }
        if (repository.existsByPlaca(dto.getPlaca())) {
            throw new ResourceAlreadyExistsException("Veiculo", "placa", dto.getPlaca());
        }
        if (dto.getModelo() == null || dto.getModelo().getId() == null) {
            String mensagem = messageSource.getMessage("erro.recurso.invalido", new Object[]{"Modelo"}, LocaleContextHolder.getLocale());
            throw new ValidationException("modelo", mensagem);
        }
        if (modeloRepository.findById(dto.getModelo().getId()).isEmpty()) {
            String mensagem = messageSource.getMessage("recurso.nao.encontrado", new Object[]{"Modelo"}, LocaleContextHolder.getLocale());
            throw new ResourceNotFoundException(mensagem);
        }
        if (dto.getAno() == null || dto.getAno() < 1886 || dto.getAno() > Year.now().getValue() + 1) {
            String mensagem = messageSource.getMessage("erro.formato.ano", null, LocaleContextHolder.getLocale());
            throw new ValidationException("ano", mensagem);
        }
        Veiculo salvo = repository.save(VeiculoMapper.toEntity(dto));
        return VeiculoMapper.toDTO(salvo);
    }

    @Transactional
    public VeiculoDTO atualizar(Long id, VeiculoDTO dto) {
        String mensagemNaoEncontrado = messageSource.getMessage("veiculo.nao.encontrado", null, LocaleContextHolder.getLocale());
        Veiculo existente = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(mensagemNaoEncontrado));
        if (dto.getPlaca() != null && !dto.getPlaca().equals(existente.getPlaca())) {
            if (repository.existsByPlaca(dto.getPlaca())) {
                throw new ResourceAlreadyExistsException("Veiculo", "placa", dto.getPlaca());
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
            String mensagem = messageSource.getMessage("veiculo.nao.encontrado", null, LocaleContextHolder.getLocale());
            throw new ResourceNotFoundException(mensagem);
        }
        repository.deleteById(id);
    }
}
