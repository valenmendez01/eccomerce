package com.uade.eccomerce.controllers.usuarios;

import java.sql.Date;

import com.uade.eccomerce.entity.Rol;
import com.uade.eccomerce.entity.Usuario;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponse {
    private Long idUsuario;
    private String email;
    private String nombre;
    private String apellido;
    private Boolean activo;
    private Date fechaCreacion;
    private Rol rol;

    public static UsuarioResponse from(Usuario usuario) {
        if (usuario == null) {
            return null;
        }

        return UsuarioResponse.builder()
                .idUsuario(usuario.getIdUsuario())
                .email(usuario.getEmail())
                .nombre(usuario.getNombre())
                .apellido(usuario.getApellido())
                .activo(usuario.getActivo())
                .fechaCreacion(usuario.getFechaCreacion())
                .rol(usuario.getRol())
                .build();
    }
}
