package com.uade.eccomerce.service.usuario;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uade.eccomerce.controllers.usuarios.UsuarioResponse;
import com.uade.eccomerce.entity.Usuario;
import com.uade.eccomerce.exceptions.usuarios.UsuarioNotFoundException;
import com.uade.eccomerce.repository.UsuarioRepository;

@Service
public class UsuarioServiceImp implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public UsuarioResponse obtenerUsuarioPorEmail(String email) throws UsuarioNotFoundException {
        if (email == null || email.isBlank()) {
            throw new UsuarioNotFoundException();
        }

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(UsuarioNotFoundException::new);

        return UsuarioResponse.from(usuario);
    }
}
