package com.uade.eccomerce.service.producto;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.eccomerce.controllers.imagenes.ImagenResponse;
import com.uade.eccomerce.controllers.productos.ProductoRequest;
import com.uade.eccomerce.controllers.productos.ProductoResponse;
import com.uade.eccomerce.entity.Categoria;
import com.uade.eccomerce.entity.Producto;
import com.uade.eccomerce.entity.Seleccion;
import com.uade.eccomerce.entity.Usuario;
import com.uade.eccomerce.exceptions.SolicitudInvalidaException;
import com.uade.eccomerce.exceptions.productos.ProductoDuplicateException;
import com.uade.eccomerce.exceptions.productos.ProductoIdInvalidoException;
import com.uade.eccomerce.exceptions.productos.ProductoNotFoundException;
import com.uade.eccomerce.exceptions.productos.filtros.CategoriaInvalidaException;
import com.uade.eccomerce.exceptions.productos.filtros.NombreInvalidoException;
import com.uade.eccomerce.exceptions.productos.filtros.PrecioInvalidoException;
import com.uade.eccomerce.exceptions.productos.filtros.SeleccionInvalidaException;
import com.uade.eccomerce.exceptions.usuarios.UsuarioNotFoundException;
import com.uade.eccomerce.repository.ProductoRepository;
import com.uade.eccomerce.repository.UsuarioRepository;

@Service
public class ProductoServiceImp implements ProductoService {
    
    @Autowired
    private ProductoRepository productoRepository;

    @Autowired 
    private UsuarioRepository usuarioRepository;

    private Usuario obtenerVendedorProducto(String emailVendedor) throws UsuarioNotFoundException {
        if (emailVendedor == null || emailVendedor.isBlank()) {
            throw new UsuarioNotFoundException();
        }

        return usuarioRepository.findByEmail(emailVendedor)
                .orElseThrow(UsuarioNotFoundException::new);
    }

    private void validarProducto(ProductoRequest request) {
        if (request == null) {
            throw new SolicitudInvalidaException("Los datos del producto son obligatorios.");
        }

        if (request.getNombre() == null || request.getNombre().trim().isEmpty()) {
            throw new SolicitudInvalidaException("El nombre del producto es obligatorio.");
        }

        if (request.getDescription() == null || request.getDescription().trim().isEmpty()) {
            throw new SolicitudInvalidaException("La descripcion del producto es obligatoria.");
        }

        if (request.getCategoria() == null) {
            throw new SolicitudInvalidaException("La categoria del producto es obligatoria.");
        }

        if (request.getPrecio() == null || request.getPrecio() <= 0) {
            throw new SolicitudInvalidaException("El precio debe ser mayor a cero.");
        }

        if (request.getStock() == null || request.getStock() < 0) {
            throw new SolicitudInvalidaException("El stock no puede ser negativo.");
        }

        if (request.getDescuento() == null || request.getDescuento() < 0 || request.getDescuento() > 100) {
            throw new SolicitudInvalidaException("El descuento debe estar entre 0 y 100.");
        }
    }

    private ProductoResponse toResponse(Producto producto) {
        // Usamos el Builder que definiste en ProductoResponse
        return ProductoResponse.builder()
            .idProducto(producto.getIdProducto())
            .nombre(producto.getNombre())
            .description(producto.getDescription())
            .precio(producto.getPrecio())
            .stock(producto.getStock())
            .disponible(producto.getStock() > 0 && producto.getActivo())
            .descuento(producto.getDescuento())
            .destacado(producto.getDestacado())
            .categoria(producto.getCategoria())
            .seleccion(producto.getSeleccion())
            .activo(producto.getActivo())
            // Mapeamos los datos del Usuario
            .idUsuario(producto.getUsuario() != null ? producto.getUsuario().getIdUsuario() : null)
            .nombreUsuario(producto.getUsuario() != null ? producto.getUsuario().getNombre() : null)
            // Mapeamos la lista de URLs de imágenes
            .imagenes(producto.getImagenes() != null ? 
                producto.getImagenes().stream()
                    .map(img -> {
                        try {
                            byte[] bytes = img.getContenido().getBytes(1, (int) img.getContenido().length());
                            return ImagenResponse.builder()
                                    .idImagen(img.getIdImagen()) // El ID es clave para el front
                                    .contenidoBase64(java.util.Base64.getEncoder().encodeToString(bytes))
                                    .build();
                        } catch (Exception e) {
                            return null;
                        }
                    }).toList() : null)
            .build();
    }

    @Transactional(readOnly = true)
    public Page<ProductoResponse> getProductos(PageRequest pageable) {
        // Buscamos los productos en el repositorio
        Page<Producto> productos = productoRepository.findByActivoTrue(pageable);
 
        // Mapeamos cada Producto a ProductoResponse
        return productos.map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public ProductoResponse getProductoById(Long id) throws ProductoIdInvalidoException, ProductoNotFoundException {

        // Validamos si el ID es nulo
        if (id == null) {
            throw new ProductoIdInvalidoException();
        }

        // Buscamos en el repositorio
        Optional<Producto> result = productoRepository.findById(id);

        // Validamos si se encontró el producto
        if (!result.isPresent() || !result.get().getActivo()) {
            throw new ProductoNotFoundException();
        }

        // Si todo está bien, devolvemos el objeto directamente
        return toResponse(result.get());
    }

    @Transactional(rollbackFor = Throwable.class)
    public ProductoResponse guardarProducto(ProductoRequest request, String emailVendedor) throws ProductoDuplicateException, UsuarioNotFoundException {
        validarProducto(request);

        // Validamos que no exista un producto con el mismo nombre
        if (productoRepository.existsByNombre(request.getNombre())) {
            throw new ProductoDuplicateException();
        }

        Usuario vendedor = obtenerVendedorProducto(emailVendedor);

        // Creamos una entidad vacía
        Producto producto = new Producto();
        
        // Seteamos los datos básicos que vienen en el Request
        producto.setNombre(request.getNombre());
        producto.setDescription(request.getDescription());
        producto.setPrecio(request.getPrecio());
        producto.setStock(request.getStock());
        producto.setDescuento(request.getDescuento());
        producto.setDestacado(request.getDestacado() != null ? request.getDestacado() : false);
        producto.setCategoria(request.getCategoria());
        producto.setSeleccion(request.getSeleccion());
        producto.setActivo(true);
        producto.setUsuario(vendedor);

        Producto guardado = productoRepository.save(producto);

        return toResponse(guardado);
    }

    @Transactional(rollbackFor = Throwable.class)
    public ProductoResponse actualizarProducto(Long id, ProductoRequest request, String emailVendedor) throws ProductoIdInvalidoException, ProductoNotFoundException, UsuarioNotFoundException {
        validarProducto(request);

        // Validamos nulidad del ID
        if (id == null) {
            throw new ProductoIdInvalidoException();
        }

        // Buscamos el producto por ID
        Optional<Producto> result = productoRepository.findById(id);

        // Validamos que exista
        if (!result.isPresent()) {
            throw new ProductoNotFoundException();
        }

        // Si existe, obtenemos el objeto
        Producto productoExistente = result.get();

        // Actualizamos los campos básicos desde el Request
        productoExistente.setNombre(request.getNombre());
        productoExistente.setDescription(request.getDescription());
        productoExistente.setPrecio(request.getPrecio());
        productoExistente.setStock(request.getStock());
        productoExistente.setDescuento(request.getDescuento());
        productoExistente.setCategoria(request.getCategoria());
        if (request.getDestacado() != null) {
            productoExistente.setDestacado(request.getDestacado());
        }
        if (request.getActivo() != null) {
            productoExistente.setActivo(request.getActivo());
        }
        if (request.getSeleccion() != null) {
            productoExistente.setSeleccion(request.getSeleccion());
        }

        productoExistente.setUsuario(obtenerVendedorProducto(emailVendedor));

        // Guardamos los cambios
        return toResponse(productoRepository.save(productoExistente));
    }

    @Transactional(rollbackFor = Throwable.class)
    public void eliminarProducto(Long id) throws ProductoNotFoundException, ProductoIdInvalidoException {
        // Validamos nulidad del ID
        if (id == null) {
            throw new ProductoIdInvalidoException();
        }
    
        // Buscamos el producto por ID
        Optional<Producto> result = productoRepository.findById(id);

        // Validamos que exista
        if (!result.isPresent()) {
            throw new ProductoNotFoundException();
        }

        // Obtenemos el objeto
        Producto p = result.get();

        // Realizamos la baja lógica
        p.setActivo(false);
        productoRepository.save(p);
    }

    @Transactional(readOnly = true)
    public Page<ProductoResponse> getProductosDelVendedor(String email, PageRequest pageable) {
        return productoRepository.findByUsuarioEmail(email, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<ProductoResponse> getProductosByCategorias(List<Categoria> categorias, PageRequest pageable)
        throws CategoriaInvalidaException {
        if (categorias == null || categorias.isEmpty()) {
            throw new CategoriaInvalidaException();
        }
        Page<Producto> result = productoRepository.findByCategoriaAndActivoTrue(categorias, pageable);

        return result.map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<ProductoResponse> getProductosBySelecciones(List<Seleccion> selecciones, PageRequest pageable)
        throws SeleccionInvalidaException {
        if (selecciones == null || selecciones.isEmpty()) {
            throw new SeleccionInvalidaException();
        }
        Page<Producto> result = productoRepository.findBySeleccionInAndActivoTrue(selecciones, pageable);
        
        return result.map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<ProductoResponse> getProductosByPrecio(Double min, Double max, PageRequest pageable) throws PrecioInvalidoException {
        
        // Validar que el precio mínimo no sea mayor que el máximo y que no sean nulos
        if (min == null || max == null || min < 0 || max < 0 || min > max) {
            throw new PrecioInvalidoException();
        }
        Page<Producto> result = productoRepository.findByPrecioBetweenAndActivoTrue(min, max, pageable);
        return result.map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<ProductoResponse> getProductosByNombre(String nombre, PageRequest pageable)
        throws NombreInvalidoException {
        // Validar que el nombre no sea nulo
        if (nombre == null) {
            throw new NombreInvalidoException();
        }

        // Realizar la búsqueda
        Page<Producto> result = productoRepository.findByNombreContainingIgnoreCaseAndActivoTrue(nombre, pageable);

        return result.map(this::toResponse);
    }

    public boolean tieneStock(Long id, Integer cantidadSolicitada) throws ProductoNotFoundException {
        if (cantidadSolicitada == null || cantidadSolicitada <= 0) {
            return false;
        }
        
        Optional<Producto> result = productoRepository.findById(id);

        // Validamos que exista
        if (!result.isPresent()) {
            throw new ProductoNotFoundException();
        }

        // Usamos .get() para acceder a los métodos de la entidad Producto
        Producto producto = result.get();
        return producto.getStock() >= cantidadSolicitada && producto.getActivo();
    }

}
