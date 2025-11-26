package com.veiculos.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import org.hibernate.annotations.CreationTimestamp;

@Entity
public class Veiculo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 7)
    private String placa;

    @Column(nullable = false, length = 50)
    private String cor;

    //precision = 10 - Até 10 dígitos totais
    //scale = 2 - 2 casas decimais (ex: 99.999.999,99)
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Column(nullable = false)
    private Integer ano;

    @Column(length = 500)
    private String descricao;

    @CreationTimestamp
    @Column(name = "data_cadastro", nullable = false, updatable = false)
    private LocalDateTime dataCadastro;

    @ManyToOne
    @JoinColumn(name = "modelo_id", nullable = false)
    private Modelo modelo;

    public Veiculo() {
    }

    public Veiculo(String placa, String cor, BigDecimal valor, Integer ano, String descricao, Modelo modelo) {
        this.placa = placa;
        this.cor = cor;
        this.valor = valor;
        this.ano = ano;
        this.descricao = descricao;
        this.modelo = modelo;
    }

    public Long getId() {
        return id;
    }

    public String getPlaca() {
        return placa;
    }

    public String getCor() {
        return cor;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public Integer getAno() {
        return ano;
    }

    public String getDescricao() {
        return descricao;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public Modelo getModelo() {
        return modelo;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setModelo(Modelo modelo) {
        this.modelo = modelo;
    }
}
