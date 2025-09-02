package com.veiculos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.veiculos.dto.EmailTemplateDTO;
import com.veiculos.service.EmailTemplate;

import jakarta.mail.MessagingException;

@RestController
public class EmailController {

    @Autowired
    private EmailTemplate emailTemplate;

    @PostMapping("/api/email")
    public ResponseEntity<String> sendEmail(@RequestBody EmailTemplateDTO emailTemplateDTO) {
        try {
            emailTemplate.sendEmailFromTemplate(emailTemplateDTO);
            return ResponseEntity.ok("E-mail enviado");
        } catch (MessagingException e) {
            return ResponseEntity.status(500).body("Erro ao enviar e-mail: " + e.getMessage());
        }
    }
}
