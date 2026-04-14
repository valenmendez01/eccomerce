package com.uade.eccomerce.controllers.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uade.eccomerce.exceptions.usuarios.UsuarioDuplicateException;
import com.uade.eccomerce.service.AuthenticationService;

import lombok.RequiredArgsConstructor;

/**
 * Controller para gestionar la autenticación y el registro de usuarios.
 * Permite la creación de nuevas cuentas y la generación de tokens JWT para el acceso seguro al sistema.
 * * Endpoints:
 * - register() - POST /api/v1/auth/register - Registra un nuevo usuario (comprador por defecto) y devuelve su token de acceso
 * - authenticate() - POST /api/v1/auth/authenticate - Autentica a un usuario existente validando sus credenciales y devuelve su token JWT
 */

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request) throws UsuarioDuplicateException {
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(service.authenticate(request));
    }
}