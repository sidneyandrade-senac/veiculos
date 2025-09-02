package com.veiculos.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.veiculos.dto.EmailTemplateDTO;
import com.veiculos.util.ReadFile;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailTemplate {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.mail.from:no-reply@example.com}")
    private String from;

    public void sendEmailFromTemplate(EmailTemplateDTO emailTemplateDTO) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();

        message.setFrom(new InternetAddress(from));
        message.setRecipients(MimeMessage.RecipientType.TO, "meuemail@exemplo.com");
        message.setSubject("Meu Assunto");

        // Read the HTML template into a String variable (from classpath resources)
        String htmlTemplate = ReadFile.readFile("templates/email-template.html");

        // Replace placeholders in the HTML template with dynamic values
        htmlTemplate = htmlTemplate.replace("${nome}", emailTemplateDTO.getNome());
        htmlTemplate = htmlTemplate.replace("${mensagem}", emailTemplateDTO.getMensagem());

        // Set the email's content to be the HTML template
        message.setContent(htmlTemplate, "text/html; charset=utf-8");

        mailSender.send(message);
    }
}
