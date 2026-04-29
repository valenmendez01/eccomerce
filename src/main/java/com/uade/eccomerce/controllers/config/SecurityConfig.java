package com.uade.eccomerce.controllers.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.uade.eccomerce.entity.Rol;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            // Permite el acceso sin necesidad de estar autenticado (cualquier rol)
            .authorizeHttpRequests(req -> req
                // Permitir la ruta interna de errores para ver las excepciones
                .requestMatchers("/error").permitAll()
                // Rutas públicas (Registro y Login)
                .requestMatchers("/api/v1/auth/**").permitAll()
                
                // Configuración de Productos
                .requestMatchers(HttpMethod.GET, "/productos/**").permitAll() // Público
                .requestMatchers(HttpMethod.POST, "/productos/**").hasAuthority(Rol.VENDEDOR.name())
                .requestMatchers(HttpMethod.PUT, "/productos/**").hasAuthority(Rol.VENDEDOR.name())

                // Configuración de Pedidos
                .requestMatchers(HttpMethod.POST, "/pedidos", "/pedidos/").hasAuthority(Rol.COMPRADOR.name())
                .requestMatchers(HttpMethod.GET, "/pedidos/usuario/**").hasAuthority(Rol.COMPRADOR.name()) // Comprador ve sus pedidos
                .requestMatchers(HttpMethod.GET, "/pedidos/{id}").hasAnyAuthority(Rol.COMPRADOR.name(), Rol.VENDEDOR.name()) // Detalle compra
                .requestMatchers(HttpMethod.GET, "/pedidos").hasAuthority(Rol.VENDEDOR.name())

                // Cualquier otra ruta requiere estar autenticado
                .anyRequest().authenticated()
            )
            // Configura la política de creación de sesiones como STATELESS para que no se cree una sesión en el servidor y se utilice el token JWT para autenticar cada solicitud
            .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
            // Configura el proveedor de autenticación para que se utilice el AuthenticationProvider personalizado que se ha definido en ApplicationConfig para autenticar a los usuarios con el nombre de usuario y la contraseña almacenados en la base de datos
            .authenticationProvider(authenticationProvider)
            // Agrega el filtro de autenticación JWT antes del filtro de autenticación de nombre de usuario y contraseña para que se ejecute antes de que se intente autenticar con el nombre de usuario y contraseña
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
