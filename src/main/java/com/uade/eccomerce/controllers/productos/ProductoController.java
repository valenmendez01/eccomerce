package com.uade.eccomerce.controllers.productos;

import com.uade.eccomerce.entity.Categoria;
import com.uade.eccomerce.service.producto.ProductoService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    // Obtener todos los productos paginados (lo pasó la profe)
    @GetMapping
    public ResponseEntity<Page<ProductoResponse>> getProductos(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        if (page == null || size == null)
            return ResponseEntity.ok(productoService.getProductos(PageRequest.of(0, Integer.MAX_VALUE)));
        return ResponseEntity.ok(productoService.getProductos(PageRequest.of(page, size)));
    }

    // Obtener un producto por ID (lo pasó la profe)
    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponse> getProductoById(@PathVariable Long id) {
        Optional<ProductoResponse> result = productoService.getProductoById(id);
        if (result.isPresent())
            return ResponseEntity.ok(result.get());

        return ResponseEntity.noContent().build();
    }

    // Crear un nuevo producto
    @PostMapping
    public ResponseEntity<ProductoResponse> crearProducto(@RequestBody ProductoRequest request) {
        ProductoResponse guardado = productoService.guardarProducto(request);
        return ResponseEntity.ok(guardado);
    }

    // Actualizar un producto
    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponse> actualizarProducto(@PathVariable Long id, @RequestBody ProductoRequest request) {
        ProductoResponse actualizado = productoService.actualizarProducto(id, request);
        
        if (actualizado != null) {
            return ResponseEntity.ok(actualizado);
        }
        return ResponseEntity.notFound().build();
    }

    // Eliminar (desactivar) un producto
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        if (productoService.getProductoById(id).isPresent()) {
            productoService.eliminarProducto(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Filtrar por Categoría
    @GetMapping("/filtrar/{categoria}")
    public ResponseEntity<Page<ProductoResponse>> getProductosByCategoria(
            @PathVariable Categoria categoria,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        
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
            @RequestParam(required = false) Integer size) {
        
        if (page == null || size == null)
            return ResponseEntity.ok(productoService.getProductosByPrecio(min, max, PageRequest.of(0, Integer.MAX_VALUE)));
        
        return ResponseEntity.ok(productoService.getProductosByPrecio(min, max, PageRequest.of(page, size)));
    }

    // Buscar por Nombre
    @GetMapping("/filtrar/nombre")
    public ResponseEntity<Page<ProductoResponse>> getProductosByNombre(
            @RequestParam String nombre,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        
        if (page == null || size == null)
            return ResponseEntity.ok(productoService.getProductosByNombre(nombre, PageRequest.of(0, Integer.MAX_VALUE)));
        
        return ResponseEntity.ok(productoService.getProductosByNombre(nombre, PageRequest.of(page, size)));
    }
}