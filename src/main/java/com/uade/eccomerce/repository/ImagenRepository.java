package com.uade.eccomerce.repository;

import com.uade.eccomerce.entity.Imagen_productos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ImagenRepository extends JpaRepository<Imagen_productos, Long> {

    // Útil para recuperar todas las fotos de un producto específico
    List<Imagen_productos> findByProductoIdProducto(Long idProducto);

    // Útil si querés borrar todas las fotos de un producto antes de subir nuevas (en un Update)
    void deleteByProductoIdProducto(Long idProducto);
}