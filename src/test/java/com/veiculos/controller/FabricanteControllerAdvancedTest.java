package com.veiculos.controller;

import com.veiculos.repository.FabricanteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes avançados de integração para FabricanteController.
 * Escopo cobre cenários:
 * 1. Criação: sucesso, duplicado, ID indevido, (campos obrigatórios / tamanho / JSON inválido - TODO)
 * 2. Leitura: lista vazia, lista com itens, buscar inexistente
 * 3. Atualização: sucesso, inexistente, duplicidade (ainda não tratada)
 * 4. Deleção: sucesso, inexistente
 * 5. Contagem preservada em falhas
 *
 * NOTA: Atualmente RuntimeException/IllegalArgumentException sem @ControllerAdvice retornam 500.
 * Quando adicionar tratamento global, ajustar expectativas para 400/404/409 conforme regra.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FabricanteControllerAdvancedTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FabricanteRepository repository;

    private String novoNomeUnico(String base) {
        return base + System.currentTimeMillis();
    }

    private String json(String nome, String pais) {
        return "{\"nome\":\"" + nome + "\",\"paisOrigem\":\"" + pais + "\"}";
    }

    // 1. Criação
    @Nested
    @DisplayName("1. Criação")
    class Criacao {
        @Test
        @DisplayName("1.1 Deve criar com sucesso")
        void criaSucesso() throws Exception {
            long before = repository.count();
            String nome = novoNomeUnico("Criar");
            mockMvc.perform(post("/api/fabricantes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json(nome, "Brasil")))
                    .andExpect(status().isCreated())
                    .andExpect(header().exists("Location"))
                    .andExpect(jsonPath("$.id").isNumber())
                    .andExpect(jsonPath("$.nome").value(nome));
            assertThat(repository.count()).isEqualTo(before + 1);
        }

        @Test
    @DisplayName("1.2 Não deve permitir duplicado (409)")
        void naoPermiteDuplicado() throws Exception {
            String nome = novoNomeUnico("Dup");
            mockMvc.perform(post("/api/fabricantes").contentType(MediaType.APPLICATION_JSON).content(json(nome, "BR")))
                    .andExpect(status().isCreated());
            long before = repository.count();
        mockMvc.perform(post("/api/fabricantes").contentType(MediaType.APPLICATION_JSON).content(json(nome, "BR")))
            .andExpect(status().isConflict());
            assertThat(repository.count()).isEqualTo(before);
        }

        @Test
    @DisplayName("1.3 Não deve aceitar ID no corpo (400)")
        void naoAceitaIdNoCorpo() throws Exception {
            long before = repository.count();
            String payload = "{\"id\":1,\"nome\":\"" + novoNomeUnico("ComId") + "\",\"paisOrigem\":\"BR\"}";
        mockMvc.perform(post("/api/fabricantes").contentType(MediaType.APPLICATION_JSON).content(payload))
            .andExpect(status().isBadRequest());
            assertThat(repository.count()).isEqualTo(before);
        }
        // TODO 1.x Campos obrigatórios / tamanho / JSON inválido quando houver validação e handler.
    }

    // 2. Leitura
    //No JUnit 5 usar @Nested em classes internas (não static) é um padrão para agrupar cenários de teste 
    @Nested
    @DisplayName("2. Leitura")
    class Leitura {
        @Test
        @DisplayName("2.1 Lista vazia retorna []")
        void listaVazia() throws Exception {
            if (repository.count() == 0) {
                mockMvc.perform(get("/api/fabricantes"))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.length()").value(0));
            }
        }

        @Test
        @DisplayName("2.2 Lista com itens")
        void listaComItens() throws Exception {
            if (repository.count() == 0) {
                mockMvc.perform(post("/api/fabricantes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json(novoNomeUnico("List"), "BR")))
                        .andExpect(status().isCreated());
            }
            mockMvc.perform(get("/api/fabricantes"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").exists());
        }

        @Test
    @DisplayName("2.3 Buscar inexistente retorna 404")
        void buscaInexistente() throws Exception {
        mockMvc.perform(get("/api/fabricantes/999999"))
            .andExpect(status().isNotFound());
        }
    }

    // 3. Atualização
    @Nested
    @DisplayName("3. Atualização")
    class Atualizacao {
        @Test
        @DisplayName("3.1 Atualiza com sucesso")
        void atualizaSucesso() throws Exception {
            String nome = novoNomeUnico("Upd");
            String loc = mockMvc.perform(post("/api/fabricantes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json(nome, "BR")))
                    .andExpect(status().isCreated())
                    .andReturn().getResponse().getHeader("Location");
            assertThat(loc).as("Location header deve estar presente").isNotNull();
            String id = loc != null ? loc.substring(loc.lastIndexOf('/') + 1) : ""; // loc não é nulo devido ao assert
            String novoNome = novoNomeUnico("Atual");
            mockMvc.perform(put("/api/fabricantes/" + id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json(novoNome, "Portugal")))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nome").value(novoNome))
                    .andExpect(jsonPath("$.paisOrigem").value("Portugal"));
        }

        @Test
    @DisplayName("3.2 Atualizar inexistente retorna 404")
        void atualizaInexistente() throws Exception {
        mockMvc.perform(put("/api/fabricantes/999999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(novoNomeUnico("Nada"), "BR")))
            .andExpect(status().isNotFound());
        }

        @Test
    @DisplayName("3.3 Atualizar para nome duplicado agora deve retornar 409 (após handler)")
        void atualizaDuplicadoHojePermite() throws Exception {
            String nome1 = novoNomeUnico("A1");
            String nome2 = novoNomeUnico("A2");
            String loc1 = mockMvc.perform(post("/api/fabricantes").contentType(MediaType.APPLICATION_JSON).content(json(nome1, "BR")))
                    .andExpect(status().isCreated()).andReturn().getResponse().getHeader("Location");
            mockMvc.perform(post("/api/fabricantes").contentType(MediaType.APPLICATION_JSON).content(json(nome2, "BR")))
                    .andExpect(status().isCreated());
            assertThat(loc1).as("Location header deve estar presente para o primeiro recurso").isNotNull();
            String id1 = loc1 != null ? loc1.substring(loc1.lastIndexOf('/') + 1) : "";
        mockMvc.perform(put("/api/fabricantes/" + id1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(nome2, "AR")))
            .andExpect(status().isConflict());
        }
    }

    // 4. Deleção
    @Nested
    @DisplayName("4. Deleção")
    class Delecao {
        @Test
    @DisplayName("4.1 Deleta com sucesso e depois 404 no GET")
        void deletaSucesso() throws Exception {
            String nome = novoNomeUnico("Del");
            String loc = mockMvc.perform(post("/api/fabricantes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json(nome, "BR")))
                    .andExpect(status().isCreated())
                    .andReturn().getResponse().getHeader("Location");
            assertThat(loc).as("Location header deve estar presente para deleção").isNotNull();
            String id = loc != null ? loc.substring(loc.lastIndexOf('/') + 1) : "";
            mockMvc.perform(delete("/api/fabricantes/" + id))
                    .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/fabricantes/" + id))
            .andExpect(status().isNotFound());
        }

    @Test
    @DisplayName("4.2 Deletar inexistente retorna 404")
        void deletaInexistente() throws Exception {
        mockMvc.perform(delete("/api/fabricantes/999999"))
            .andExpect(status().isNotFound());
        }
    }
}
