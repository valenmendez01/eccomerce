package com.uade.eccomerce.repository;

import com.uade.eccomerce.entity.Categoria;
import com.uade.eccomerce.entity.Producto;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // Listado de productos activos
    Page<Producto> findByActivoTrue(PageRequest pageable);

    // Verificar si existe un producto con el mismo nombre (para evitar duplicados)
    boolean existsByNombre(String nombre);
    
    // Filtrar por multi-categorías
    @Query("SELECT p FROM Producto p WHERE p.categoria IN :categorias AND p.activo = true")
    Page<Producto> findByCategoriaAndActivoTrue(@Param("categorias") List<Categoria> categorias, PageRequest pageable);

    // Filtrar por rango de precio y que estén activos
    Page<Producto> findByPrecioBetweenAndActivoTrue(Double min, Double max, PageRequest pageable);
    
    // Buscar por nombre (para el buscador del catálogo)
    Page<Producto> findByNombreContainingIgnoreCaseAndActivoTrue(String nombre, PageRequest pageable);

    Page<Producto> findByUsuarioEmail(String email, PageRequest pageable);

}
