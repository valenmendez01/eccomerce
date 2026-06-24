package com.uade.eccomerce.service.producto;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.uade.eccomerce.controllers.productos.ProductoRequest;
import com.uade.eccomerce.controllers.productos.ProductoResponse;
import com.uade.eccomerce.entity.Categoria;
import com.uade.eccomerce.entity.Seleccion;
import com.uade.eccomerce.exceptions.productos.ProductoDuplicateException;
import com.uade.eccomerce.exceptions.productos.ProductoIdInvalidoException;
import com.uade.eccomerce.exceptions.productos.ProductoNotFoundException;
import com.uade.eccomerce.exceptions.productos.filtros.CategoriaInvalidaException;
import com.uade.eccomerce.exceptions.productos.filtros.NombreInvalidoException;
import com.uade.eccomerce.exceptions.productos.filtros.PrecioInvalidoException;
import com.uade.eccomerce.exceptions.productos.filtros.SeleccionInvalidaException;
import com.uade.eccomerce.exceptions.usuarios.UsuarioNotFoundException;

public interface ProductoService {

    public Page<ProductoResponse> getProductos(PageRequest pageable);

    public Page<ProductoResponse> getProductosDestacados(PageRequest pageable);

    public ProductoResponse getProductoById(Long id) throws ProductoIdInvalidoException, ProductoNotFoundException;

    public ProductoResponse guardarProducto(ProductoRequest request, String emailVendedor) throws ProductoDuplicateException, UsuarioNotFoundException;

    public ProductoResponse actualizarProducto(Long id, ProductoRequest request, String emailVendedor) throws ProductoIdInvalidoException, ProductoNotFoundException, UsuarioNotFoundException;

    public void eliminarProducto(Long id, String emailVendedor) throws ProductoNotFoundException, ProductoIdInvalidoException, UsuarioNotFoundException;

    public Page<ProductoResponse> getProductosDelVendedor(String email, PageRequest pageable);

    Page<ProductoResponse> getProductosByCategorias(List<Categoria> categorias, PageRequest pageable) throws CategoriaInvalidaException;

    Page<ProductoResponse> getProductosBySelecciones(List<Seleccion> selecciones, PageRequest pageable) throws SeleccionInvalidaException;

    public Page<ProductoResponse> getProductosByPrecio(Double min, Double max, PageRequest pageable) throws PrecioInvalidoException;

    public Page<ProductoResponse> getProductosByNombre(String nombre, PageRequest pageable) throws NombreInvalidoException;

    boolean tieneStock(Long id, Integer cantidadSolicitada) throws ProductoNotFoundException;

    Page<ProductoResponse> getProductosByFiltros(String nombre, List<Categoria> categorias, List<Seleccion> selecciones, Double min, Double max, PageRequest pageable);

}
