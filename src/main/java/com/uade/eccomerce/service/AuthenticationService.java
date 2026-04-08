package com.uade.eccomerce.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.uade.eccomerce.controllers.auth.AuthenticationRequest;
import com.uade.eccomerce.controllers.auth.AuthenticationResponse;
import com.uade.eccomerce.controllers.auth.RegisterRequest;
import com.uade.eccomerce.controllers.config.JwtService;
import com.uade.eccomerce.entity.Usuario;
import com.uade.eccomerce.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
        private final UsuarioRepository repository;
        private final PasswordEncoder passwordEncoder;
        private final JwtService jwtService;
        private final AuthenticationManager authenticationManager;

        public AuthenticationResponse register(RegisterRequest request) {
            // Crear el usuario
            var usuario = Usuario.builder()
                .nombre(request.getFirstname())
                .apellido(request.getLastname())
                .email(request.getEmail())
                .contrasena(passwordEncoder.encode(request.getPassword()))
                .rol(request.getRole())
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
                    request.getPassword()));
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
