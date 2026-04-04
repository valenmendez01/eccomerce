package com.uade.eccomerce.service.producto;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.uade.eccomerce.entity.Categoria;
import com.uade.eccomerce.entity.Producto;
import com.uade.eccomerce.repository.ProductoRepository;

public class ProductoServiceImp implements ProductoService {
    
    @Autowired
    private ProductoRepository productoRepository;

    public Page<Producto> getProductos(PageRequest pageable) {
        return productoRepository.findAll(pageable);
    }

    public Optional<Producto> getProductoById(Long id) {
        return productoRepository.findById(id);
    }

    public Producto guardarProducto(Producto producto) {
        return productoRepository.save(producto);
    }

    public void eliminarProducto(Long id) {
        productoRepository.findById(id).ifPresent(p -> {
            p.setActivo(false);
            productoRepository.save(p);
        });
    }

    public Page<Producto> getProductosByCategoria(Categoria categoria, PageRequest pageable) {
        return productoRepository.findByCategoriaAndActivoTrue(categoria, pageable);
    }

    public Page<Producto> getProductosByPrecio(Double min, Double max, PageRequest pageable) {
        return productoRepository.findByPrecioBetweenAndActivoTrue(min, max, pageable);
    }

    public Page<Producto> getProductosByNombre(String nombre, PageRequest pageable) {
        return productoRepository.findByNombreContainingIgnoreCaseAndActivoTrue(nombre, pageable);
    }

}
