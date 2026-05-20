package com.uade.eccomerce.controllers.usuarios;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uade.eccomerce.controllers.ApiResponse;
import com.uade.eccomerce.entity.Usuario;
import com.uade.eccomerce.exceptions.usuarios.UsuarioNotFoundException;
import com.uade.eccomerce.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UsuarioResponse>> obtenerUsuarioActual(Authentication authentication)
            throws UsuarioNotFoundException {
        if (authentication == null) {
            throw new UsuarioNotFoundException();
        }

        Usuario usuario = usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow(UsuarioNotFoundException::new);

        return ResponseEntity.ok(new ApiResponse<>("Usuario autenticado obtenido", UsuarioResponse.from(usuario)));
    }
}
