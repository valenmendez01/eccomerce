package com.uade.eccomerce.service.producto;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.uade.eccomerce.controllers.productos.ProductoRequest;
import com.uade.eccomerce.entity.Categoria;
import com.uade.eccomerce.entity.Imagen_productos;
import com.uade.eccomerce.entity.Producto;
import com.uade.eccomerce.entity.Usuario;
import com.uade.eccomerce.repository.ImagenRepository;
import com.uade.eccomerce.repository.ProductoRepository;
import com.uade.eccomerce.repository.UsuarioRepository;

@Service
public class ProductoServiceImp implements ProductoService {
    
    @Autowired
    private ProductoRepository productoRepository;

    @Autowired 
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ImagenRepository imagenRepository;

    public Page<Producto> getProductos(PageRequest pageable) {
        return productoRepository.findByActivoTrue(pageable);
    }

    public Optional<Producto> getProductoById(Long id) {
        return productoRepository.findById(id);
    }

    public Producto guardarProducto(ProductoRequest request) {
        // Creamos una entidad vacía
        Producto producto = new Producto();
        
        // Seteamos los datos básicos que vienen en el Request
        producto.setNombre(request.getNombre());
        producto.setDescription(request.getDescription());
        producto.setPrecio(request.getPrecio());
        producto.setStock(request.getStock());
        producto.setDescuento(request.getDescuento());
        producto.setCategoria(request.getCategoria());
        producto.setActivo(true);

        // Buscamos al Usuario real por el ID
        Usuario vendedor = usuarioRepository.findById(request.getIdUsuario())
            .orElseThrow(() -> new RuntimeException("Vendedor no encontrado"));
        producto.setUsuario(vendedor);

        Producto guardado = productoRepository.save(producto);

        // Manejo de imágenes (si mandaron URLs)
        if (request.getUrlsImagenes() != null) {
            for (String url : request.getUrlsImagenes()) {
                Imagen_productos img = new Imagen_productos();
                img.setUrl(url);
                img.setProducto(guardado);
                imagenRepository.save(img);
            }
        }
        return guardado;
    }

    public Producto actualizarProducto(Long id, ProductoRequest request) {
        // Buscamos el producto existente por ID
        Producto productoExistente = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // Actualizamos los campos básicos desde el Request
        productoExistente.setNombre(request.getNombre());
        productoExistente.setDescription(request.getDescription());
        productoExistente.setPrecio(request.getPrecio());
        productoExistente.setStock(request.getStock());
        productoExistente.setDescuento(request.getDescuento());
        productoExistente.setCategoria(request.getCategoria());

        // Si el ID de usuario cambió, buscamos al nuevo vendedor
        if (request.getIdUsuario() != null) {
            Usuario nuevoVendedor = usuarioRepository.findById(request.getIdUsuario())
                    .orElseThrow(() -> new RuntimeException("Vendedor no encontrado"));
            productoExistente.setUsuario(nuevoVendedor);
        }

        // Guardamos los cambios
        return productoRepository.save(productoExistente);
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
