package com.veiculos.controller;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.veiculos.dto.FabricanteDTO;
import com.veiculos.service.FabricanteService;

@RestController
@RequestMapping("/api/fabricantes")
public class FabricanteController {

    @Autowired
    private FabricanteService service;


    @GetMapping
    public List<FabricanteDTO> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public FabricanteDTO buscar(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<FabricanteDTO> criar(@RequestBody FabricanteDTO dto) {
        FabricanteDTO criado = service.criar(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(criado.getId()).toUri();
        return ResponseEntity.created(location).body(criado);
        //return ResponseEntity.created(null).body(criado);
    }

    @PutMapping("/{id}")
    public FabricanteDTO atualizar(@PathVariable Long id, @RequestBody FabricanteDTO dto) {
        return service.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
