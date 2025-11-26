package com.veiculos.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Home {

    @GetMapping("/")
    public String index() {
        return "Voce esta na mapeamento inicial";
    }

    @GetMapping("/home")
    public String home() {
        return "voce esta mapeamento HOME";
    }

    //http://localhost:8080/soma/4/7
    @GetMapping("/soma/{num1}/{num2}")
    public String soma(@PathVariable int num1, @PathVariable int num2) {
        return "voce esta mapeamento SOMA de " + num1 + " + " + num2 + "  = " + (num1 + num2);
    }

}
