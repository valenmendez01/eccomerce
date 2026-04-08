package com.uade.eccomerce.service.producto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.uade.eccomerce.controllers.productos.ProductoRequest;
import com.uade.eccomerce.controllers.productos.ProductoResponse;
import com.uade.eccomerce.entity.Categoria;
import com.uade.eccomerce.exceptions.productos.ProductoDuplicateException;
import com.uade.eccomerce.exceptions.productos.ProductoIdInvalidoException;
import com.uade.eccomerce.exceptions.productos.ProductoNotFoundException;
import com.uade.eccomerce.exceptions.productos.filtros.CategoriaInvalidaException;
import com.uade.eccomerce.exceptions.productos.filtros.NombreInvalidoException;
import com.uade.eccomerce.exceptions.productos.filtros.PrecioInvalidoException;
import com.uade.eccomerce.exceptions.usuarios.UsuarioNotFoundException;

public interface ProductoService {

    public Page<ProductoResponse> getProductos(PageRequest pageable) throws ProductoNotFoundException;

    public ProductoResponse getProductoById(Long id) throws ProductoIdInvalidoException, ProductoNotFoundException;

    public ProductoResponse guardarProducto(ProductoRequest request) throws ProductoDuplicateException, UsuarioNotFoundException;

    public ProductoResponse actualizarProducto(Long id, ProductoRequest request) throws ProductoIdInvalidoException, ProductoNotFoundException, UsuarioNotFoundException;

    public void eliminarProducto(Long id) throws ProductoNotFoundException, ProductoIdInvalidoException;

    public Page<ProductoResponse> getProductosByCategoria(Categoria categoria, PageRequest pageable) throws CategoriaInvalidaException, ProductoNotFoundException;

    public Page<ProductoResponse> getProductosByPrecio(Double min, Double max, PageRequest pageable) throws PrecioInvalidoException;

    public Page<ProductoResponse> getProductosByNombre(String nombre, PageRequest pageable) throws NombreInvalidoException, ProductoNotFoundException;

}