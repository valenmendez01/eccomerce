package com.uade.eccomerce.controllers.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uade.eccomerce.controllers.ApiResponse;
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
    public ResponseEntity<ApiResponse<AuthenticationResponse>> register(
            @RequestBody RegisterRequest request) throws UsuarioDuplicateException {
        AuthenticationResponse response = service.register(request);
        return ResponseEntity.ok(new ApiResponse<>("Usuario registrado exitosamente", response));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> authenticate(
            @RequestBody AuthenticationRequest request) {
        AuthenticationResponse response = service.authenticate(request);
        return ResponseEntity.ok(new ApiResponse<>("Login exitoso", response));
    }
}