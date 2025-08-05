package com.veiculos.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Modelo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @ManyToOne
    @JoinColumn(name = "fabricante_id", nullable = false)
    private Fabricante fabricante;

    public Modelo() {
    }

    public Modelo(String nome, Fabricante fabricante) {
        this.nome = nome;
        this.fabricante = fabricante;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public Fabricante getFabricante() {
        return fabricante;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setFabricante(Fabricante fabricante) {
        this.fabricante = fabricante;
    }
}
