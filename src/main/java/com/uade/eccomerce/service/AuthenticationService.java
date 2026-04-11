package com.uade.eccomerce.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.eccomerce.controllers.auth.AuthenticationRequest;
import com.uade.eccomerce.controllers.auth.AuthenticationResponse;
import com.uade.eccomerce.controllers.auth.RegisterRequest;
import com.uade.eccomerce.controllers.config.JwtService;
import com.uade.eccomerce.entity.Rol;
import com.uade.eccomerce.entity.Usuario;
import com.uade.eccomerce.exceptions.usuarios.UsuarioDuplicateException;
import com.uade.eccomerce.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional(rollbackFor = Throwable.class)
    public AuthenticationResponse register(RegisterRequest request) throws UsuarioDuplicateException {
        
        // Validar si el email ya existe
        if (repository.existsByEmail(request.getEmail())) {
            throw new UsuarioDuplicateException();
        }
        
        // Crear el usuario
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

        // Generar el token JWT
        var jwtToken = jwtService.generateToken(usuario);
        return AuthenticationResponse.builder()
            .accessToken(jwtToken)
            .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getContrasena()));
        // Va a la base de datos a buscar el usuario
        var usuario = repository.findByEmail(request.getEmail())
            .orElseThrow();
        // Genera el token JWT
        var jwtToken = jwtService.generateToken(usuario);
        return AuthenticationResponse.builder()
            .accessToken(jwtToken)
            .build();
    }
}
