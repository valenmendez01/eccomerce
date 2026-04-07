package com.uade.eccomerce.service.producto;

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
import com.uade.eccomerce.exceptions.productos.ProductoDuplicateException;
import com.uade.eccomerce.exceptions.productos.ProductoIdInvalidoException;
import com.uade.eccomerce.exceptions.productos.ProductoNotFoundException;
import com.uade.eccomerce.exceptions.productos.filtros.CategoriaInvalidaException;
import com.uade.eccomerce.exceptions.productos.filtros.NombreInvalidoException;
import com.uade.eccomerce.exceptions.productos.filtros.PrecioInvalidoException;
import com.uade.eccomerce.exceptions.usuarios.UsuarioNotFoundException;
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

    public Page<Producto> getProductos(PageRequest pageable) throws ProductoNotFoundException {
        // Buscamos los productos en el repositorio
        Page<Producto> productos = productoRepository.findByActivoTrue(pageable);
        if (productos.isEmpty()) {
            throw new ProductoNotFoundException();
        }
        return productos;
    }

    public Producto getProductoById(Long id) throws ProductoIdInvalidoException, ProductoNotFoundException {

        // Validamos si el ID es nulo
        if (id == null) {
            throw new ProductoIdInvalidoException();
        }

        // Buscamos en el repositorio
        Optional<Producto> result = productoRepository.findById(id);

        // Validamos si se encontró el producto
        if (!result.isPresent()) {
            throw new ProductoNotFoundException();
        }

        // Si todo está bien, devolvemos el objeto directamente
        return result.get();
    }

    public Producto guardarProducto(ProductoRequest request) throws ProductoDuplicateException, UsuarioNotFoundException {

        // Validamos que no exista un producto con el mismo nombre
        if (productoRepository.existsByNombre(request.getNombre())) {
            throw new ProductoDuplicateException();
        }

        // Validamos que el vendedor exista
        Long idVendedor = request.getIdUsuario();
        if (idVendedor == null) {
            throw new UsuarioNotFoundException(); 
        }
        // Buscamos al vendedor en el repositorio
        Optional<Usuario> userResult = usuarioRepository.findById(idVendedor);
        if (!userResult.isPresent()) {
            throw new UsuarioNotFoundException();
        }

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
        producto.setUsuario(userResult.get());

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

    public Producto actualizarProducto(Long id, ProductoRequest request) throws ProductoIdInvalidoException, ProductoNotFoundException, UsuarioNotFoundException {

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

        // Si el ID de usuario cambió, buscamos al nuevo vendedor
        Long idVendedor = request.getIdUsuario();
        if (idVendedor != null) {
            Optional<Usuario> userResult = usuarioRepository.findById(idVendedor);
            
            if (!userResult.isPresent()) {
                throw new UsuarioNotFoundException();
            }
            
            productoExistente.setUsuario(userResult.get());
        }

        // Guardamos los cambios
        return productoRepository.save(productoExistente);
    }

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

    public Page<Producto> getProductosByCategoria(Categoria categoria, PageRequest pageable)
        throws CategoriaInvalidaException, ProductoNotFoundException {
        // Validar que la categoría no sea nula
        if (categoria == null) {
            throw new CategoriaInvalidaException();
        }

        // Realizar la búsqueda
        Page<Producto> result = productoRepository.findByCategoriaAndActivoTrue(categoria, pageable);

        // Validar si la página está vacía
        if (result.isEmpty()) {
            throw new ProductoNotFoundException();
        }

        return result;
    }

    public Page<Producto> getProductosByPrecio(Double min, Double max, PageRequest pageable) throws PrecioInvalidoException {
        
        // Validar que el precio mínimo no sea mayor que el máximo y que no sean nulos
        if (min == null || max == null || min > max) {
            throw new PrecioInvalidoException();
        }
        return productoRepository.findByPrecioBetweenAndActivoTrue(min, max, pageable);
    }

    public Page<Producto> getProductosByNombre(String nombre, PageRequest pageable)
        throws NombreInvalidoException, ProductoNotFoundException {
        // Validar que el nombre no sea nulo
        if (nombre == null) {
            throw new NombreInvalidoException();
        }

        // Realizar la búsqueda
        Page<Producto> result = productoRepository.findByNombreContainingIgnoreCaseAndActivoTrue(nombre, pageable);

        // Validar si la página está vacía
        if (result.isEmpty()) {
            throw new ProductoNotFoundException();
        }

        return result;
    }

}
