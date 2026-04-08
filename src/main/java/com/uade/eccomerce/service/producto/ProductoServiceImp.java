package com.uade.eccomerce.service.producto;

import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.uade.eccomerce.controllers.productos.ProductoResponse;
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

        // Método de mapeo (Helper)
    private ProductoResponse mapToResponse(Producto producto) {
        return ProductoResponse.builder()
                .idProducto(producto.getIdproducto())
                .nombre(producto.getNombre())
                .description(producto.getDescription())
                .precio(producto.getPrecio())
                .stock(producto.getStock())
                .descuento(producto.getDescuento())
                .categoria(producto.getCategoria())
                .activo(producto.getActivo())
                .idUsuario(producto.getUsuario().getId_usuario())
                .nombreUsuario(producto.getUsuario().getNombre()) 
                .urlsImagenes(producto.getImages() != null ? 
                        producto.getImages().stream().map(Imagen_productos::getUrl).collect(Collectors.toList()) : null)
                .build();
    }

    public Page<ProductoResponse> getProductos(PageRequest pageable) {
        return productoRepository.findByActivoTrue(pageable).map(this::mapToResponse);
    }

    public Optional<ProductoResponse> getProductoById(Long id) {
        return productoRepository.findById(id).map(this::mapToResponse);
    }

    public ProductoResponse guardarProducto(ProductoRequest request) {
        Producto producto = new Producto();
        
        producto.setNombre(request.getNombre());
        producto.setDescription(request.getDescription());
        producto.setPrecio(request.getPrecio());
        producto.setStock(request.getStock());
        producto.setDescuento(request.getDescuento());
        producto.setCategoria(request.getCategoria());
        producto.setActivo(true);

        Usuario vendedor = usuarioRepository.findById(request.getIdUsuario())
            .orElseThrow(() -> new RuntimeException("Vendedor no encontrado"));
        producto.setUsuario(vendedor);

        Producto guardado = productoRepository.save(producto);

        if (request.getUrlsImagenes() != null) {
            for (String url : request.getUrlsImagenes()) {
                Imagen_productos img = new Imagen_productos();
                img.setUrl(url);
                img.setProducto(guardado);
                imagenRepository.save(img);
            }
        }
        
        return mapToResponse(guardado);
    }

    public ProductoResponse actualizarProducto(Long id, ProductoRequest request) {
        Producto productoExistente = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        productoExistente.setNombre(request.getNombre());
        productoExistente.setDescription(request.getDescription());
        productoExistente.setPrecio(request.getPrecio());
        productoExistente.setStock(request.getStock());
        productoExistente.setDescuento(request.getDescuento());
        productoExistente.setCategoria(request.getCategoria());

        if (request.getIdUsuario() != null) {
            Usuario nuevoVendedor = usuarioRepository.findById(request.getIdUsuario())
                    .orElseThrow(() -> new RuntimeException("Vendedor no encontrado"));
            productoExistente.setUsuario(nuevoVendedor);
        }

        return mapToResponse(productoRepository.save(productoExistente));
    }

    public void eliminarProducto(Long id) {
        productoRepository.findById(id).ifPresent(p -> {
            p.setActivo(false);
            productoRepository.save(p);
        });
    }

    public Page<ProductoResponse> getProductosByCategoria(Categoria categoria, PageRequest pageable) {
        return productoRepository.findByCategoriaAndActivoTrue(categoria, pageable).map(this::mapToResponse);
    }

    public Page<ProductoResponse> getProductosByPrecio(Double min, Double max, PageRequest pageable) {
        return productoRepository.findByPrecioBetweenAndActivoTrue(min, max, pageable).map(this::mapToResponse);
    }

    public Page<ProductoResponse> getProductosByNombre(String nombre, PageRequest pageable) {
        return productoRepository.findByNombreContainingIgnoreCaseAndActivoTrue(nombre, pageable).map(this::mapToResponse);
    }

}