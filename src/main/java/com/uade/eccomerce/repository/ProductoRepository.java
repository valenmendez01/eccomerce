package com.uade.eccomerce.repository;

import com.uade.eccomerce.entity.Categoria;
import com.uade.eccomerce.entity.Producto;
import com.uade.eccomerce.entity.Seleccion;

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

    Page<Producto> findByDestacadoTrueAndActivoTrue(PageRequest pageable);

    // Verificar si existe un producto con el mismo nombre (para evitar duplicados)
    boolean existsByNombre(String nombre);
    
    // Filtrar por multi-categorías
    @Query("SELECT p FROM Producto p WHERE p.categoria IN :categorias AND p.activo = true")
    Page<Producto> findByCategoriaAndActivoTrue(@Param("categorias") List<Categoria> categorias, PageRequest pageable);

    // Filtrar por multi-selecciones
    @Query("SELECT p FROM Producto p WHERE p.seleccion IN :selecciones AND p.activo = true")
    Page<Producto> findBySeleccionInAndActivoTrue(@Param("selecciones") List<Seleccion> selecciones, PageRequest pageable);

    // Filtrar por rango de precio y que estén activos
    @Query("SELECT p FROM Producto p WHERE (p.precio * (1 - p.descuento / 100.0)) BETWEEN :min AND :max AND p.activo = true")
    Page<Producto> findByPrecioConDescuentoBetweenAndActivoTrue(@Param("min") Double min, @Param("max") Double max, PageRequest pageable);
    
    // Buscar por nombre (para el buscador del catálogo)
    Page<Producto> findByNombreContainingIgnoreCaseAndActivoTrue(String nombre, PageRequest pageable);

    Page<Producto> findByUsuarioEmail(String email, PageRequest pageable);

    @Query("SELECT p FROM Producto p WHERE p.activo = true " +
       "AND (:nombre IS NULL OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) " +
       "AND (:categorias IS NULL OR p.categoria IN :categorias) " +
       "AND (:selecciones IS NULL OR p.seleccion IN :selecciones) " +
       "AND (p.precio * (1 - p.descuento / 100.0)) BETWEEN :min AND :max")
    Page<Producto> findByFiltros(
        @Param("nombre") String nombre,
        @Param("categorias") List<Categoria> categorias,
        @Param("selecciones") List<Seleccion> selecciones,
        @Param("min") Double min,
        @Param("max") Double max,
        PageRequest pageable);
    long countByDestacadoTrueAndActivoTrue();

}
