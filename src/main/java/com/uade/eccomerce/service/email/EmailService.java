package com.uade.eccomerce.service.email;

public interface EmailService {
    void enviarEmail(String destinatario, String asunto, String mensaje);
    void enviarEmailHtml(String destinatario, String asunto, String html);
}
