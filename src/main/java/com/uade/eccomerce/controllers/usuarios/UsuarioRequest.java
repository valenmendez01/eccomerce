package com.uade.eccomerce.controllers.usuarios;

import lombok.Data;

@Data
public class UsuarioRequest {
    private String username;
    private String email;
    private String contrasena;
    private String nombre;
    private String apellido;
}