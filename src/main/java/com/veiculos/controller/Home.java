package com.veiculos.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Home {

    @GetMapping("/home")
    public String index() {
        return "Bem-vindo ao sistema de gerenciamento de veículos!";
    }

}
