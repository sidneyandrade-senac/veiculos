package com.veiculos.controller;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.veiculos.dto.FabricanteDTO;
import com.veiculos.response.ApiResponse;
import com.veiculos.service.FabricanteService;

@RestController
@RequestMapping("/api/fabricantes")
public class FabricanteController {

    @Autowired
    private FabricanteService service;


    @GetMapping
    public ResponseEntity<ApiResponse<List<FabricanteDTO>>> listar() {
        List<FabricanteDTO> fabricantes = service.listar();
        ApiResponse<List<FabricanteDTO>> response = ApiResponse.success(
            fabricantes, 
            "Lista de fabricantes recuperada com sucesso", 
            HttpStatus.OK.value()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FabricanteDTO>> buscar(@PathVariable Long id) {
        FabricanteDTO fabricante = service.buscarPorId(id);
        ApiResponse<FabricanteDTO> response = ApiResponse.success(
            fabricante, 
            "Fabricante encontrado com sucesso", 
            HttpStatus.OK.value()
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<FabricanteDTO>> criar(@RequestBody FabricanteDTO dto) {
        FabricanteDTO criado = service.criar(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(criado.getId()).toUri();
        
        ApiResponse<FabricanteDTO> response = ApiResponse.created(
            criado, 
            "Fabricante criado com sucesso"
        );
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FabricanteDTO>> atualizar(@PathVariable Long id, @RequestBody FabricanteDTO dto) {
        FabricanteDTO atualizado = service.atualizar(id, dto);
        ApiResponse<FabricanteDTO> response = ApiResponse.success(
            atualizado, 
            "Fabricante atualizado com sucesso", 
            HttpStatus.OK.value()
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletar(@PathVariable Long id) {
        service.deletar(id);
        ApiResponse<Void> response = ApiResponse.success(
            null, 
            "Fabricante deletado com sucesso", 
            HttpStatus.OK.value()
        );
        return ResponseEntity.ok(response);
    }
}
