package com.veiculos.controller;

import com.veiculos.repository.FabricanteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
// application-test.properties || application-config.properties se @ActiveProfiles("config")
@ActiveProfiles("test")
class FabricanteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // somente para realizar a contagem, para validar o teste
    @Autowired
    private FabricanteRepository repository;

    @Test
    @DisplayName("Deve cadastrar fabricante com sucesso")
    void deveCadastrarFabricante() throws Exception {
        String nomeGerado = "Toyota" + System.currentTimeMillis();
        String json = "{\"nome\":\"" + nomeGerado + "\",\"paisOrigem\":\"Japao\"}"; // nome único por execução

        long before = repository.count();

        mockMvc.perform(post("/api/fabricantes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value("Fabricante criado com sucesso"))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.id").isNumber())
                .andExpect(jsonPath("$.data.nome").value(nomeGerado))
                .andExpect(jsonPath("$.data.paisOrigem").value("Japao"))
                .andExpect(jsonPath("$.timestamp").exists());

        assertThat(repository.count()).isEqualTo(before + 1);
    }

    @Test
    @DisplayName("Deve listar fabricantes incluindo o recém criado")
    void deveListarFabricantes() throws Exception {
        // garante ao menos um registro
        if (repository.count() == 0) {
            mockMvc.perform(post("/api/fabricantes")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"nome\":\"Honda\",\"paisOrigem\":\"Japao\"}"))
                    .andExpect(status().isCreated());
        }

        mockMvc.perform(get("/api/fabricantes")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Lista de fabricantes recuperada com sucesso"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("Deve buscar fabricante por id")
    void deveBuscarPorId() throws Exception {
        // cria um fabricante específico para o teste
        String nome = "MarcaBusca" + System.currentTimeMillis();
        String body = "{\"nome\":\"" + nome + "\",\"paisOrigem\":\"Brasil\"}";
        //location = http://localhost/api/fabricantes/139
        String location = mockMvc.perform(post("/api/fabricantes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getHeader("Location");
        assertThat(location).as("Location header deve estar presente").isNotNull();
        // extrai id da URL /api/fabricantes/{id}
        String id = location.substring(location.lastIndexOf('/') + 1);

        mockMvc.perform(get("/api/fabricantes/" + id)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Fabricante encontrado com sucesso"))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.id").value(Long.parseLong(id)))
                .andExpect(jsonPath("$.data.nome").value(nome))
                .andExpect(jsonPath("$.data.paisOrigem").value("Brasil"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("Deve atualizar fabricante existente")
    void deveAtualizarFabricante() throws Exception {
        String nomeOriginal = "MarcaEdit" + System.currentTimeMillis();
        String createJson = "{\"nome\":\"" + nomeOriginal + "\",\"paisOrigem\":\"Argentina\"}";
        String location = mockMvc.perform(post("/api/fabricantes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getHeader("Location");
        assertThat(location).isNotNull();
        String id = location.substring(location.lastIndexOf('/') + 1);

        String nomeAtualizado = nomeOriginal + "Atualizado";
        String updateJson = "{\"nome\":\"" + nomeAtualizado + "\",\"paisOrigem\":\"Chile\"}";

        mockMvc.perform(put("/api/fabricantes/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Fabricante atualizado com sucesso"))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.id").value(Long.parseLong(id)))
                .andExpect(jsonPath("$.data.nome").value(nomeAtualizado))
                .andExpect(jsonPath("$.data.paisOrigem").value("Chile"))
                .andExpect(jsonPath("$.timestamp").exists());

        // valida no banco
        Long idLong = Long.parseLong(id);
        var encontrado = repository.findById(idLong).orElseThrow();
        assertThat(encontrado.getNome()).isEqualTo(nomeAtualizado);
        assertThat(encontrado.getPaisOrigem()).isEqualTo("Chile");
    }

    @Test
    @DisplayName("Deve deletar fabricante e não encontrá-lo mais")
    void deveDeletarFabricante() throws Exception {
        String nome = "MarcaDel" + System.currentTimeMillis();
        String body = "{\"nome\":\"" + nome + "\",\"paisOrigem\":\"Chile\"}";
        String location = mockMvc.perform(post("/api/fabricantes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getHeader("Location");
        assertThat(location).isNotNull();
        String id = location.substring(location.lastIndexOf('/') + 1);

        mockMvc.perform(delete("/api/fabricantes/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Fabricante deletado com sucesso"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.timestamp").exists());

        // valida que não existe mais
        assertThat(repository.findById(Long.parseLong(id))).isEmpty();
    }
}
