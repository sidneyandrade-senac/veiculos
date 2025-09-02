package com.veiculos.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.veiculos.dto.VeiculoDTO;
import com.veiculos.entity.Veiculo;

public final class VeiculoMapper {

    private VeiculoMapper() {}

    public static VeiculoDTO toDTO(Veiculo e) {
        if (e == null) return null;
        return new VeiculoDTO(
            e.getId(), e.getPlaca(), e.getCor(), e.getValor(), e.getAno(), e.getDescricao(), e.getDataCadastro(), e.getModelo()
        );
    }

    public static Veiculo toEntity(VeiculoDTO d) {
        if (d == null) return null;
        Veiculo v = new Veiculo();
        v.setId(d.getId());
        v.setPlaca(d.getPlaca());
        v.setCor(d.getCor());
        v.setValor(d.getValor());
        v.setAno(d.getAno());
        v.setDescricao(d.getDescricao());
        v.setModelo(d.getModelo());
        return v;
    }

    public static List<VeiculoDTO> toDTOList(List<Veiculo> list) {
        return list == null ? List.of() : list.stream().map(VeiculoMapper::toDTO).collect(Collectors.toList());
    }

    public static List<Veiculo> toEntityList(List<VeiculoDTO> list) {
        return list == null ? List.of() : list.stream().map(VeiculoMapper::toEntity).collect(Collectors.toList());
    }
}
