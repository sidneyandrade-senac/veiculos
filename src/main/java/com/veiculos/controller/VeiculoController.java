package com.veiculos.controller;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.veiculos.dto.VeiculoDTO;
import com.veiculos.service.VeiculoService;

@RestController
@RequestMapping("/api/veiculos")
public class VeiculoController {

    @Autowired
    private VeiculoService service;

    //Personalizável via query params: ?page=0&size=10&sort=dataCadastro,desc ou outro campo (ex.: placa,asc).
    //QUANDO PASSADO VALOR SEM OS PARÂMETROS PEGA O DEFAULT
    //para paginar usar ?page=1 ... ?page=2
    @GetMapping
    public Page<VeiculoDTO> listar(
            @PageableDefault(size = 4, sort = "dataCadastro", direction = Sort.Direction.DESC) 
            Pageable pageable
    ) {
        return service.listar(pageable);
    }

    //lista todos sem paginação
    @GetMapping("/todos")
    public List<VeiculoDTO> listarTodos() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public VeiculoDTO buscar(@PathVariable Long id) { 
        return service.buscarPorId(id);
    }

    @GetMapping("/placa/{placa}")
    public VeiculoDTO buscarPorPlaca(@PathVariable String placa) {
        return service.buscarPorPlaca(placa);
    }

    @GetMapping("/existe/{placa}")
    public boolean existePorPlaca(@PathVariable String placa) {
        return service.existePorPlaca(placa);
    }   

    @GetMapping("/placa")
    public List<VeiculoDTO> buscarPorPlacaParcial(@RequestParam("trecho_placa") String trecho_placa) {
        return service.buscarPorPlacaParcial(trecho_placa);
    }

    @PostMapping
    public ResponseEntity<VeiculoDTO> criar(@RequestBody VeiculoDTO dto) {
        VeiculoDTO criado = service.criar(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(criado.getId()).toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @PutMapping("/{id}")
    public VeiculoDTO atualizar(@PathVariable Long id, @RequestBody VeiculoDTO dto) {
        return service.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
