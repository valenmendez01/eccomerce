package com.uade.eccomerce.repository;

import com.uade.eccomerce.entity.Categoria;
import com.uade.eccomerce.entity.Producto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // Listado de productos activos
    Page<Producto> findByActivoTrue(PageRequest pageable);

    // Verificar si existe un producto con el mismo nombre (para evitar duplicados)
    boolean existsByNombre(String nombre);
    
    // Filtrar por categoría
    Page<Producto> findByCategoriaAndActivoTrue(Categoria categoria, PageRequest pageable);

    // Filtrar por rango de precio y que estén activos
    Page<Producto> findByPrecioBetweenAndActivoTrue(Double min, Double max, PageRequest pageable);
    
    // Buscar por nombre (para el buscador del catálogo)
    Page<Producto> findByNombreContainingIgnoreCaseAndActivoTrue(String nombre, PageRequest pageable);

}