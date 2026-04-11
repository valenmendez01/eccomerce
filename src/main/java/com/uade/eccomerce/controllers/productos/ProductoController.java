package com.uade.eccomerce.controllers.productos;

import com.uade.eccomerce.entity.Categoria;
import com.uade.eccomerce.exceptions.productos.ProductoDuplicateException;
import com.uade.eccomerce.exceptions.productos.ProductoIdInvalidoException;
import com.uade.eccomerce.exceptions.productos.ProductoNotFoundException;
import com.uade.eccomerce.exceptions.productos.filtros.CategoriaInvalidaException;
import com.uade.eccomerce.exceptions.productos.filtros.NombreInvalidoException;
import com.uade.eccomerce.exceptions.productos.filtros.PrecioInvalidoException;
import com.uade.eccomerce.exceptions.usuarios.UsuarioNotFoundException;
import com.uade.eccomerce.service.producto.ProductoService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    // Obtener todos los productos paginados
    @GetMapping
    public ResponseEntity<Page<ProductoResponse>> getProductos(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size)
            throws ProductoNotFoundException {
        if (page == null || size == null)
            return ResponseEntity.ok(productoService.getProductos(PageRequest.of(0, Integer.MAX_VALUE)));
        return ResponseEntity.ok(productoService.getProductos(PageRequest.of(page, size)));
    }

    // Obtener un producto por ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponse> getProductoById(@PathVariable Long id) 
            throws ProductoIdInvalidoException, ProductoNotFoundException {
        ProductoResponse producto = productoService.getProductoById(id);
        return ResponseEntity.ok(producto);
    }

    // Crear un nuevo producto
    @PostMapping
    public ResponseEntity<ProductoResponse> guardarProducto(@RequestBody ProductoRequest request) 
            throws ProductoDuplicateException, UsuarioNotFoundException {

        return ResponseEntity.ok(productoService.guardarProducto(request));
    }

    // Actualizar un producto
    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponse> actualizarProducto(@PathVariable Long id, @RequestBody ProductoRequest request)
            throws ProductoNotFoundException, ProductoIdInvalidoException, UsuarioNotFoundException {

        ProductoResponse actualizado = productoService.actualizarProducto(id, request);
        return ResponseEntity.ok(actualizado);
    }

    // Eliminar (desactivar) un producto
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id)
            throws ProductoNotFoundException, ProductoIdInvalidoException {

        productoService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }

    // Filtrar por Categoría
    @GetMapping("/filtrar/{categoria}")
    public ResponseEntity<Page<ProductoResponse>> getProductosByCategoria(
            @PathVariable Categoria categoria,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) 
            throws CategoriaInvalidaException, ProductoNotFoundException {
        
        if (page == null || size == null)
            return ResponseEntity.ok(productoService.getProductosByCategoria(categoria, PageRequest.of(0, Integer.MAX_VALUE)));
        
        return ResponseEntity.ok(productoService.getProductosByCategoria(categoria, PageRequest.of(page, size)));
    }

    // Filtrar por Rango de Precio
    @GetMapping("/filtrar/precio")
    public ResponseEntity<Page<ProductoResponse>> getProductosByPrecio(
            @RequestParam Double min,
            @RequestParam Double max,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size)
            throws PrecioInvalidoException {
        
        if (page == null || size == null)
            return ResponseEntity.ok(productoService.getProductosByPrecio(min, max, PageRequest.of(0, Integer.MAX_VALUE)));
        
        return ResponseEntity.ok(productoService.getProductosByPrecio(min, max, PageRequest.of(page, size)));
    }

    // Buscar por Nombre
    @GetMapping("/filtrar/nombre")
    public ResponseEntity<Page<ProductoResponse>> getProductosByNombre(
            @RequestParam String nombre,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size)
            throws NombreInvalidoException, ProductoNotFoundException {
        
        if (page == null || size == null)
            return ResponseEntity.ok(productoService.getProductosByNombre(nombre, PageRequest.of(0, Integer.MAX_VALUE)));
        
        return ResponseEntity.ok(productoService.getProductosByNombre(nombre, PageRequest.of(page, size)));
    }
}