package com.uade.eccomerce.controllers.usuarios;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uade.eccomerce.controllers.ApiResponse;
import com.uade.eccomerce.exceptions.usuarios.UsuarioNotFoundException;
import com.uade.eccomerce.service.usuario.UsuarioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping("/actual")
    public ResponseEntity<ApiResponse<UsuarioResponse>> obtenerUsuarioActual(Authentication authentication)
            throws UsuarioNotFoundException {
        String email = authentication == null ? null : authentication.getName();
        return ResponseEntity.ok(new ApiResponse<>("Usuario autenticado obtenido",
                usuarioService.obtenerUsuarioPorEmail(email)));
    }
}
