package com.uade.eccomerce.service.producto;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.uade.eccomerce.controllers.productos.ProductoRequest;
import com.uade.eccomerce.controllers.productos.ProductoResponse;
import com.uade.eccomerce.entity.Categoria;

public interface ProductoService {

    public Page<ProductoResponse> getProductos(PageRequest pageable);

    public Optional<ProductoResponse> getProductoById(Long id);

    public ProductoResponse guardarProducto(ProductoRequest request);

    public ProductoResponse actualizarProducto(Long id, ProductoRequest request);

    public void eliminarProducto(Long id);

    public Page<ProductoResponse> getProductosByCategoria(Categoria categoria, PageRequest pageable);

    public Page<ProductoResponse> getProductosByPrecio(Double min, Double max, PageRequest pageable);

    public Page<ProductoResponse> getProductosByNombre(String nombre, PageRequest pageable);
    
}