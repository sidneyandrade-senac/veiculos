package com.veiculos.controller;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.veiculos.dto.VeiculoDTO;
import com.veiculos.response.ApiResponse;
import com.veiculos.service.VeiculoService;

@RestController
@RequestMapping("/api/veiculos")
public class VeiculoController {

    @Autowired
    private VeiculoService service;

    /**
     * Lista veiculos com paginacao.
     * Personalizavel via query params: ?page=0&size=10&sort=dataCadastro,desc ou outro campo (ex.: placa,asc).
     * Quando passado valor sem os parametros pega o default.
     * Para paginar usar ?page=1 ... ?page=2
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<VeiculoDTO>>> listar(
            @PageableDefault(size = 4, sort = "dataCadastro", direction = Sort.Direction.DESC) 
            Pageable pageable
    ) {
        Page<VeiculoDTO> veiculos = service.listar(pageable);
        
        ApiResponse<Page<VeiculoDTO>> response = ApiResponse.success(
            veiculos, 
            "Lista paginada de veiculos recuperada com sucesso", 
            HttpStatus.OK.value()
        );
        
        // Adiciona metadados de paginacao
        response.addMeta("totalElements", veiculos.getTotalElements());
        response.addMeta("totalPages", veiculos.getTotalPages());
        response.addMeta("currentPage", veiculos.getNumber());
        response.addMeta("pageSize", veiculos.getSize());
        response.addMeta("hasNext", veiculos.hasNext());
        response.addMeta("hasPrevious", veiculos.hasPrevious());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Lista todos os veiculos sem paginacao.
     */
    @GetMapping("/todos")
    public ResponseEntity<ApiResponse<List<VeiculoDTO>>> listarTodos() {
        List<VeiculoDTO> veiculos = service.listar();
        ApiResponse<List<VeiculoDTO>> response = ApiResponse.success(
            veiculos, 
            "Lista completa de veiculos recuperada com sucesso", 
            HttpStatus.OK.value()
        );
        response.addMeta("total", veiculos.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VeiculoDTO>> buscar(@PathVariable Long id) {
        VeiculoDTO veiculo = service.buscarPorId(id);
        ApiResponse<VeiculoDTO> response = ApiResponse.success(
            veiculo, 
            "Veiculo encontrado com sucesso", 
            HttpStatus.OK.value()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/placa/{placa}")
    public ResponseEntity<ApiResponse<VeiculoDTO>> buscarPorPlaca(@PathVariable String placa) {
        VeiculoDTO veiculo = service.buscarPorPlaca(placa);
        ApiResponse<VeiculoDTO> response = ApiResponse.success(
            veiculo, 
            "Veiculo encontrado com sucesso", 
            HttpStatus.OK.value()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/existe/{placa}")
    public ResponseEntity<ApiResponse<Boolean>> existePorPlaca(@PathVariable String placa) {
        boolean existe = service.existePorPlaca(placa);
        ApiResponse<Boolean> response = ApiResponse.success(
            existe, 
            existe ? "Placa ja cadastrada" : "Placa disponivel", 
            HttpStatus.OK.value()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/placa")
    public ResponseEntity<ApiResponse<List<VeiculoDTO>>> buscarPorPlacaParcial(@RequestParam("trecho_placa") String trecho_placa) {
        List<VeiculoDTO> veiculos = service.buscarPorPlacaParcial(trecho_placa);
        ApiResponse<List<VeiculoDTO>> response = ApiResponse.success(
            veiculos, 
            "Veiculos encontrados com sucesso", 
            HttpStatus.OK.value()
        );
        response.addMeta("total", veiculos.size());
        response.addMeta("filtro", trecho_placa);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<VeiculoDTO>> criar(@RequestBody VeiculoDTO dto) {
        VeiculoDTO criado = service.criar(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(criado.getId()).toUri();
        
        ApiResponse<VeiculoDTO> response = ApiResponse.created(
            criado, 
            "Veiculo criado com sucesso"
        );
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<VeiculoDTO>> atualizar(@PathVariable Long id, @RequestBody VeiculoDTO dto) {
        VeiculoDTO atualizado = service.atualizar(id, dto);
        ApiResponse<VeiculoDTO> response = ApiResponse.success(
            atualizado, 
            "Veiculo atualizado com sucesso", 
            HttpStatus.OK.value()
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletar(@PathVariable Long id) {
        service.deletar(id);
        ApiResponse<Void> response = ApiResponse.success(
            null, 
            "Veiculo deletado com sucesso", 
            HttpStatus.OK.value()
        );
        return ResponseEntity.ok(response);
    }
}
