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
 * Servi?o respons?vel pelas opera??es de neg?cio com Fabricante.
 *
 * Sobre @Transactional:
 * - A anota??o @Transactional controla a transa??o do banco durante a execu??o do m?todo.
 * - Por padr?o, @Transactional abre uma transa??o de leitura/escrita com propaga??o REQUIRED
 *   (reutiliza a transa??o existente ou cria uma nova) e isolamento padr?o do provedor de dados.
 * - Commit: se o m?todo concluir sem lan?ar exce??es em tempo de execu??o (unchecked), a transa??o ? confirmada.
 * - Rollback: por padr?o ocorre em RuntimeException e Error; para exce??es checadas, configure rollbackFor se necess?rio.
 * - readOnly = true indica que o m?todo n?o deve realizar altera??es (escritas) no banco. Essa dica permite otimiza??es
 *   no provedor JPA e no banco (p. ex., evita flush autom?tico), sendo apropriado para consultas.
 *
 * Observa??o sobre efeitos colaterais dentro da transa??o:
 * - Opera??es externas (como envio de e-mail) n?o participam da transa??o do banco. Se o e-mail for enviado dentro
 *   do m?todo e depois ocorrer um rollback, o e-mail j? ter? sido enviado. Para consist?ncia, considere publicar um
 *   evento p?s-commit (TransactionSynchronization) ou usar padr?o Outbox.
 */

/**
 * Lista todos os fabricantes.
 * Transa??o: @Transactional(readOnly = true) — otimiza para leitura e evita escritas acidentais.
 *
 * @return lista de FabricanteDTO.
 */
 
/**
 * Busca um fabricante pelo ID.
 * Transa??o: @Transactional(readOnly = true) — apenas leitura.
 *
 * @param id identificador do fabricante.
 * @return FabricanteDTO correspondente.
 * @throws RuntimeException se o fabricante n?o for encontrado.
 */
 
/**
 * Cria um novo fabricante, validando aus?ncia de ID e unicidade do nome.
 * Ap?s persistir com sucesso, dispara um e-mail de notifica??o.
 * Transa??o: @Transactional (leitura/escrita).
 * - Em caso de RuntimeException, a transa??o ? revertida.
 * - Aten??o: o envio de e-mail ocorre fora do escopo transacional do banco e pode ter sido conclu?do
 *   mesmo se houver rollback posterior; para garantir consist?ncia, use eventos p?s-commit ou outbox.
 *
 * @param dto dados do fabricante a ser criado (id deve ser nulo).
 * @return FabricanteDTO persistido.
 * @throws IllegalArgumentException se o ID for informado ou se j? existir fabricante com o mesmo nome.
 */
 
/**
 * Atualiza nome e pa?s de origem de um fabricante existente.
 * Transa??o: @Transactional (leitura/escrita).
 *
 * @param id identificador do fabricante a atualizar.
 * @param dto dados a atualizar.
 * @return FabricanteDTO atualizado.
 * @throws RuntimeException se o fabricante n?o for encontrado.
 */
 
/**
 * Remove um fabricante pelo ID.
 * Transa??o: @Transactional (leitura/escrita).
 *
 * @param id identificador do fabricante a remover.
 * @throws RuntimeException se o fabricante n?o for encontrado.
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
            emailService.enviarEmail("senac@yahoo.com.br", "Novo Fabricante Criado", "Um novo fabricante foi criado: " + salvo.getNome());
        }
        return FabricanteMapper.toDTO(salvo);
    }

    @Transactional
    public FabricanteDTO atualizar(Long id, FabricanteDTO dto) {
        Fabricante existente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fabricante n?o encontrado"));
        existente.setNome(dto.getNome());
        existente.setPaisOrigem(dto.getPaisOrigem());
        return FabricanteMapper.toDTO(repository.save(existente));
    }

    @Transactional
    public void deletar(Long id) {
        if (repository.existsById(id)) {
            throw new RuntimeException("Fabricante n?o encontrado");
        }
        repository.deleteById(id);
    }
}
