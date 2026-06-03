package com.uade.eccomerce.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import com.uade.eccomerce.controllers.auth.AuthenticationRequest;
import com.uade.eccomerce.controllers.auth.AuthenticationResponse;
import com.uade.eccomerce.controllers.auth.RegisterRequest;
import com.uade.eccomerce.controllers.config.JwtService;
import com.uade.eccomerce.controllers.usuarios.UsuarioResponse;
import com.uade.eccomerce.entity.Rol;
import com.uade.eccomerce.entity.Usuario;
import com.uade.eccomerce.exceptions.usuarios.UsuarioDuplicateException;
import com.uade.eccomerce.repository.UsuarioRepository;
import com.uade.eccomerce.service.email.EmailService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    @Transactional(rollbackFor = Throwable.class)
    public AuthenticationResponse register(RegisterRequest request) throws UsuarioDuplicateException {
        if (repository.existsByEmail(request.getEmail())) {
            throw new UsuarioDuplicateException();
        }

        var usuario = Usuario.builder()
            .nombre(request.getNombre())
            .apellido(request.getApellido())
            .email(request.getEmail())
            .contrasena(passwordEncoder.encode(request.getContrasena()))
            .rol(Rol.COMPRADOR)
            .activo(true)
            .fechaCreacion(new java.sql.Date(System.currentTimeMillis()))
            .build();

        repository.save(usuario);
        enviarEmailRegistro(usuario);

        var jwtToken = jwtService.generateToken(usuario);
        return AuthenticationResponse.builder()
            .accessToken(jwtToken)
            .usuario(UsuarioResponse.from(usuario))
            .build();
    }

    private void enviarEmailRegistro(Usuario usuario) {
        try {
            String nombre = HtmlUtils.htmlEscape(usuario.getNombre());
            String mensaje = "<div style='margin:0;padding:32px;background:#f7f5ef;font-family:Arial,sans-serif;color:#142b10;'>"
                + "<div style='max-width:620px;margin:0 auto;background:#fffdf8;border:1px solid #ead8bb;border-radius:18px;overflow:hidden;'>"
                + "<div style='background:#142b10;padding:26px 28px;text-align:center;'>"
                + "<img src='cid:logoFigulect' alt='FIGULLECT' style='width:190px;max-width:80%;height:auto;' />"
                + "</div>"
                + "<div style='padding:32px;'>"
                + "<p style='margin:0 0 8px;color:#caa56e;font-size:13px;letter-spacing:4px;text-transform:uppercase;font-weight:bold;'>Cuenta registrada correctamente</p>"
                + "<h1 style='margin:0 0 18px;font-size:30px;line-height:1.15;color:#142b10;'>Bienvenido a FIGULLECT</h1>"
                + "<p style='font-size:16px;line-height:1.7;margin:0 0 14px;'>Hola " + nombre + ",</p>"
                + "<p style='font-size:16px;line-height:1.7;margin:0 0 14px;'>Tu cuenta en FIGULLECT fue creada correctamente.</p>"
                + "<p style='font-size:16px;line-height:1.7;margin:0 0 14px;'>Ya podés iniciar sesión, explorar el catálogo y empezar a completar tu colección.</p>"
                + "<p style='font-size:17px;line-height:1.7;margin:22px 0;padding:18px;border-left:4px solid #caa56e;background:#f7f2e9;font-weight:bold;'>Cada figurita cuenta, y la tuya empieza ahora.</p>"
                + "<p style='font-size:16px;line-height:1.7;margin:0;'>Gracias por sumarte a FIGULLECT.</p>"
                + "<p style='font-size:15px;line-height:1.7;margin:24px 0 0;color:#5f6f5b;'>Equipo FIGULLECT</p>"
                + "</div>"
                + "</div>"
                + "</div>";

            emailService.enviarEmailHtml(
                usuario.getEmail(),
                "Bienvenido a FIGULLECT - Cuenta registrada correctamente",
                mensaje);
        } catch (Exception e) {
            System.err.println("No se pudo enviar el email de registro: " + e.getMessage());
        }
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getContrasena()));

        var usuario = repository.findByEmail(request.getEmail())
            .orElseThrow();

        var jwtToken = jwtService.generateToken(usuario);
        return AuthenticationResponse.builder()
            .accessToken(jwtToken)
            .usuario(UsuarioResponse.from(usuario))
            .build();
    }
}
