package com.veiculos.dto;

public class EmailTemplateDTO {
    private String nome;
    private String mensagem;

    public EmailTemplateDTO() {}

    public EmailTemplateDTO(String nome, String mensagem) {
        this.nome = nome;
        this.mensagem = mensagem;
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }
}
