package com.uade.eccomerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uade.eccomerce.entity.Usuario;
import java.util.Optional;


@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    //Optional<Usuario> findById(Long Id);
    
    Optional<Usuario> findByEmail(String email);
}
