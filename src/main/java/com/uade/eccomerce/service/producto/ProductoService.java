package com.uade.eccomerce.service.producto;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.uade.eccomerce.entity.Categoria;
import com.uade.eccomerce.entity.Producto;

public interface ProductoService {

    public Page<Producto> getProductos(PageRequest pageable);

    public Optional<Producto> getProductoById(Long id);

    public Producto guardarProducto(Producto producto);

    public void eliminarProducto(Long id);

    public Page<Producto> getProductosByCategoria(Categoria categoria, PageRequest pageable);

    public Page<Producto> getProductosByPrecio(Double min, Double max, PageRequest pageable);

    public Page<Producto> getProductosByNombre(String nombre, PageRequest pageable);
    
}
