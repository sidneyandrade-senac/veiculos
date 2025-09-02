package com.veiculos.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.veiculos.dto.ModeloDTO;
import com.veiculos.entity.Modelo;

public final class ModeloMapper {

    private ModeloMapper() {}

    public static ModeloDTO toDTO(Modelo entity) {
        if (entity == null) return null;
        return new ModeloDTO(entity.getId(), entity.getNome(), entity.getFabricante());
    }

    public static Modelo toEntity(ModeloDTO dto) {
        if (dto == null) return null;
        Modelo modelo = new Modelo();
        modelo.setId(dto.getId());
        modelo.setNome(dto.getNome());
        modelo.setFabricante(dto.getFabricante());
        return modelo;
    }

    public static List<ModeloDTO> toDTOList(List<Modelo> list) {
        return list == null ? List.of() : list.stream().map(ModeloMapper::toDTO).collect(Collectors.toList());
    }

    public static List<Modelo> toEntityList(List<ModeloDTO> list) {
        return list == null ? List.of() : list.stream().map(ModeloMapper::toEntity).collect(Collectors.toList());
    }
}