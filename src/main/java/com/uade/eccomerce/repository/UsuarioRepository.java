package com.uade.eccomerce.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uade.eccomerce.entity.Usuario;
import java.util.Optional;


@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Page<Usuario> findByActivoTrye(PageRequest pageable);

    Optional<Usuario> findByIdAndActivo(Long Id);
    
    Optional<Usuario> findByEmailAndActivo(String email);
}
