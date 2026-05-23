package com.uade.eccomerce.service.usuario;

import com.uade.eccomerce.controllers.usuarios.UsuarioResponse;
import com.uade.eccomerce.exceptions.usuarios.UsuarioNotFoundException;

public interface UsuarioService {
    UsuarioResponse obtenerUsuarioPorEmail(String email) throws UsuarioNotFoundException;
}
