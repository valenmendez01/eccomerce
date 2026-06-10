package com.uade.eccomerce.service.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServiceImp implements EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String emailRemitente;

    @Async
    public void enviarEmail(String destinatario, String asunto, String mensaje) {
        try {
            SimpleMailMessage email = new SimpleMailMessage();
            email.setFrom(emailRemitente);
            email.setTo(destinatario);
            email.setSubject(asunto);
            email.setText(mensaje);

            javaMailSender.send(email);
        } catch (Exception e) {
            System.err.println("No se pudo enviar el email a " + destinatario + ": " + e.getMessage());
        }
    }

    @Async
    public void enviarEmailHtml(String destinatario, String asunto, String html) {
        try {
            MimeMessage mensaje = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

            helper.setFrom(emailRemitente);
            helper.setTo(destinatario);
            helper.setSubject(asunto);
            helper.setText(html, true);
            helper.addInline("logoFigulect", new ClassPathResource("mail/logo-figullect.png"));

            javaMailSender.send(mensaje);
        } catch (Exception e) {
            System.err.println("No se pudo enviar el email HTML a " + destinatario + ": " + e.getMessage());
        }
    }
}
