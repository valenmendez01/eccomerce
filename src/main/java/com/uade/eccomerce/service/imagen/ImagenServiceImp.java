package com.uade.eccomerce.service.imagen;

import java.util.List;

import javax.sql.rowset.serial.SerialBlob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.uade.eccomerce.entity.ImagenProductos;
import com.uade.eccomerce.entity.Producto;
import com.uade.eccomerce.exceptions.AccesoProductoNoAutorizadoException;
import com.uade.eccomerce.exceptions.imagenes.ImagenNotFoundException;
import com.uade.eccomerce.exceptions.productos.ProductoNotFoundException;
import com.uade.eccomerce.repository.ImagenRepository;
import com.uade.eccomerce.repository.ProductoRepository;

@Service
public class ImagenServiceImp implements ImagenService {
    @Autowired
    private ImagenRepository imagenRepository;
    @Autowired
    private ProductoRepository productoRepository;

    private String obtenerNombreArchivo(Long idProducto, MultipartFile archivo) {
        String nombreOriginal = archivo.getOriginalFilename();
        String nombreSeguro = nombreOriginal == null || nombreOriginal.isBlank()
                ? "producto-" + idProducto + "-imagen"
                : nombreOriginal;

        return nombreSeguro.length() > 255 ? nombreSeguro.substring(0, 255) : nombreSeguro;
    }

    private Producto obtenerProductoDelVendedor(Long idProducto, String emailVendedor)
            throws ProductoNotFoundException {
        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(ProductoNotFoundException::new);

        if (emailVendedor == null
                || producto.getUsuario() == null
                || !emailVendedor.equals(producto.getUsuario().getEmail())) {
            throw new AccesoProductoNoAutorizadoException();
        }

        return producto;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void agregarImagenesAProducto(
            Long idProducto,
            List<MultipartFile> archivos,
            String emailVendedor)
            throws ProductoNotFoundException, java.io.IOException, java.sql.SQLException {
        Producto producto = obtenerProductoDelVendedor(idProducto, emailVendedor);

        // Iteramos sobre la lista de archivos (imagenes)
        for (MultipartFile archivo : archivos) {
            if (!archivo.isEmpty()) { // Validamos que no esté vacío
                ImagenProductos img = new ImagenProductos();
                img.setContenido(new SerialBlob(archivo.getBytes()));
                img.setNombreArchivo(obtenerNombreArchivo(idProducto, archivo));
                img.setProducto(producto); 
                imagenRepository.save(img);
            }
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public void eliminarImagen(Long idProducto, Long idImagen, String emailVendedor)
            throws ImagenNotFoundException, ProductoNotFoundException {
        obtenerProductoDelVendedor(idProducto, emailVendedor);

        ImagenProductos imagen = imagenRepository.findById(idImagen)
                .orElseThrow(ImagenNotFoundException::new);
        if (imagen.getProducto() == null
                || !idProducto.equals(imagen.getProducto().getIdProducto())) {
            throw new ImagenNotFoundException();
        }

        imagenRepository.delete(imagen);
    }
}
