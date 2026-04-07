package com.uade.eccomerce.controllers.usuarios;

import com.uade.eccomerce.entity.Usuario;
import com.uade.eccomerce.service.usuario.UsuarioService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // Obtener todos los usuarios paginados
    @GetMapping
    public ResponseEntity<Page<Usuario>> getUsuarios(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {

        if (page == null || size == null)
            return ResponseEntity.ok(usuarioService.getUsuarios(PageRequest.of(0, Integer.MAX_VALUE)));

        return ResponseEntity.ok(usuarioService.getUsuarios(PageRequest.of(page, size)));
    }

    // Obtener usuario por ID
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> getUsuarioById(@PathVariable Long id) {
        Optional<Usuario> result = usuarioService.getUsuarioById(id);

        if (result.isPresent())
            return ResponseEntity.ok(result.get());

        return ResponseEntity.noContent().build();
    }

    // Obtener usuario por email
    @GetMapping("/email")
    public ResponseEntity<Usuario> getUsuarioByEmail(@RequestParam String email) {
        Optional<Usuario> result = usuarioService.getUsuarioByEmail(email);

        if (result.isPresent())
            return ResponseEntity.ok(result.get());

        return ResponseEntity.noContent().build();
    }

    // Crear usuario
    @PostMapping
    public ResponseEntity<Usuario> crearUsuario(@RequestBody UsuarioRequest request) {
        Usuario guardado = usuarioService.ingresarUsuario(request);
        return ResponseEntity.ok(guardado);
    }

    // Actualizar usuario (parcial)
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizarUsuario(
            @PathVariable Long id,
            @RequestBody UsuarioRequest request) {

        Usuario actualizado = usuarioService.actualizarUsuario(id, request);

        if (actualizado != null) {
            return ResponseEntity.ok(actualizado);
        }

        return ResponseEntity.notFound().build();
    }

    // Cambiar contraseña
    @PutMapping("/{id}/password")
    public ResponseEntity<Usuario> cambiarContrasena(
            @PathVariable Long id,
            @RequestBody CambiarContraRequest request) {

        Usuario actualizado = usuarioService.cambiarContrasena(id, request);

        if (actualizado != null) {
            return ResponseEntity.ok(actualizado);
        }

        return ResponseEntity.notFound().build();
    }

    // Eliminar (lógico)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        if (usuarioService.getUsuarioById(id).isPresent()) {
            usuarioService.eliminarUsuario(id);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
}