package com.veiculos.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.veiculos.dto.FabricanteDTO;
import com.veiculos.entity.Fabricante;
import com.veiculos.mapper.FabricanteMapper;
import com.veiculos.repository.FabricanteRepository;

/**
 * Serviço responsável pelas operações de negócio com Fabricante.
 *
 * Sobre @Transactional:
 * - A anotação @Transactional controla a transação do banco durante a execução do método.
 * - Por padrão, @Transactional abre uma transação de leitura/escrita com propagação REQUIRED
 *   (reutiliza a transação existente ou cria uma nova) e isolamento padrão do provedor de dados.
 * - Commit: se o método concluir sem lançar exceções em tempo de execução (unchecked), a transação é confirmada.
 * - Rollback: por padrão ocorre em RuntimeException e Error; para exceções checadas, configure rollbackFor se necessário.
 * - readOnly = true indica que o método não deve realizar alterações (escritas) no banco. Essa dica permite otimizações
 *   no provedor JPA e no banco (p. ex., evita flush automático), sendo apropriado para consultas.
 *
 * Observação sobre efeitos colaterais dentro da transação:
 * - Operações externas (como envio de e-mail) não participam da transação do banco. Se o e-mail for enviado dentro
 *   do método e depois ocorrer um rollback, o e-mail já terá sido enviado. Para consistência, considere publicar um
 *   evento pós-commit (TransactionSynchronization) ou usar padrão Outbox.
 */

/**
 * Lista todos os fabricantes.
 * Transação: @Transactional(readOnly = true) — otimiza para leitura e evita escritas acidentais.
 *
 * @return lista de FabricanteDTO.
 */
 
/**
 * Busca um fabricante pelo ID.
 * Transação: @Transactional(readOnly = true) — apenas leitura.
 *
 * @param id identificador do fabricante.
 * @return FabricanteDTO correspondente.
 * @throws RuntimeException se o fabricante não for encontrado.
 */
 
/**
 * Cria um novo fabricante, validando ausência de ID e unicidade do nome.
 * Após persistir com sucesso, dispara um e-mail de notificação.
 * Transação: @Transactional (leitura/escrita).
 * - Em caso de RuntimeException, a transação é revertida.
 * - Atenção: o envio de e-mail ocorre fora do escopo transacional do banco e pode ter sido concluído
 *   mesmo se houver rollback posterior; para garantir consistência, use eventos pós-commit ou outbox.
 *
 * @param dto dados do fabricante a ser criado (id deve ser nulo).
 * @return FabricanteDTO persistido.
 * @throws IllegalArgumentException se o ID for informado ou se já existir fabricante com o mesmo nome.
 */
 
/**
 * Atualiza nome e país de origem de um fabricante existente.
 * Transação: @Transactional (leitura/escrita).
 *
 * @param id identificador do fabricante a atualizar.
 * @param dto dados a atualizar.
 * @return FabricanteDTO atualizado.
 * @throws RuntimeException se o fabricante não for encontrado.
 */
 
/**
 * Remove um fabricante pelo ID.
 * Transação: @Transactional (leitura/escrita).
 *
 * @param id identificador do fabricante a remover.
 * @throws RuntimeException se o fabricante não for encontrado.
 */
@Service
public class FabricanteService {

    @Autowired
    private FabricanteRepository repository;

    @Autowired
    private EmailService emailService;;

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
        if(salvo.getId() != null) {
            // Aqui você pode chamar o serviço de email para enviar a notificação
            //emailService.enviarEmail("sidney.mind@yahoo.com.br", "Novo Fabricante Criado", "Um novo fabricante foi criado: " + salvo.getNome());
        }
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
