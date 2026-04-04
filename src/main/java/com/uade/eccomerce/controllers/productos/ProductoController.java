package com.uade.eccomerce.controllers.productos;

import com.uade.eccomerce.entity.Producto;
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
    public ResponseEntity<Page<Producto>> getProductos(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        if (page == null || size == null)
            return ResponseEntity.ok(productoService.getProductos(PageRequest.of(0, Integer.MAX_VALUE)));
        return ResponseEntity.ok(productoService.getProductos(PageRequest.of(page, size)));
    }

    // Obtener un producto por ID (lo pasó la profe)
    @GetMapping("/{id}")
    public ResponseEntity<Producto> getProductoById(@PathVariable Long Id) {
        Optional<Producto> result = productoService.getProductoById(Id);
        if (result.isPresent())
            return ResponseEntity.ok(result.get());

        return ResponseEntity.noContent().build();
    }

    // Crear un nuevo producto
    @PostMapping
    public Producto crearProducto(@RequestBody Producto producto) {
        return productoService.guardarProducto(producto);
    }

    // Actualizar un producto
    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizarProducto(@PathVariable Long id, @RequestBody Producto detallesProducto) {

        Optional<Producto> productoOptional = productoService.getProductoById(id);

        if (productoOptional.isPresent()) {

            Producto producto = productoOptional.get();

            producto.setNombre(detallesProducto.getNombre());
            producto.setDescription(detallesProducto.getDescription());
            producto.setPrecio(detallesProducto.getPrecio());
            producto.setStock(detallesProducto.getStock());
            producto.setDescuento(detallesProducto.getDescuento());
            producto.setCategoria(detallesProducto.getCategoria());

            Producto actualizado = productoService.guardarProducto(producto);
            return ResponseEntity.ok(actualizado);
        } else {
            return ResponseEntity.notFound().build();
        }
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
}