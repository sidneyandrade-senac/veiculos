package com.veiculos.controller;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.veiculos.dto.ModeloDTO;
import com.veiculos.response.ApiResponse;
import com.veiculos.service.ModeloService;

@RestController
@RequestMapping("/api/modelos")
public class ModeloController {

    @Autowired
    private ModeloService service;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ModeloDTO>>> listar() {
        List<ModeloDTO> modelos = service.listar();
        ApiResponse<List<ModeloDTO>> response = ApiResponse.success(
            modelos, 
            "Lista de modelos recuperada com sucesso", 
            HttpStatus.OK.value()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ModeloDTO>> buscar(@PathVariable Long id) {
        ModeloDTO modelo = service.buscarPorId(id);
        ApiResponse<ModeloDTO> response = ApiResponse.success(
            modelo, 
            "Modelo encontrado com sucesso", 
            HttpStatus.OK.value()
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ModeloDTO>> criar(@RequestBody ModeloDTO dto) {
        ModeloDTO criado = service.criar(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(criado.getId()).toUri();
        
        ApiResponse<ModeloDTO> response = ApiResponse.created(
            criado, 
            "Modelo criado com sucesso"
        );
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ModeloDTO>> atualizar(@PathVariable Long id, @RequestBody ModeloDTO dto) {
        ModeloDTO atualizado = service.atualizar(id, dto);
        ApiResponse<ModeloDTO> response = ApiResponse.success(
            atualizado, 
            "Modelo atualizado com sucesso", 
            HttpStatus.OK.value()
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletar(@PathVariable Long id) {
        service.deletar(id);
        ApiResponse<Void> response = ApiResponse.success(
            null, 
            "Modelo deletado com sucesso", 
            HttpStatus.OK.value()
        );
        return ResponseEntity.ok(response);
    }
}