package com.uade.eccomerce.service.imagen;

import java.util.List;
import java.util.Optional;

import javax.sql.rowset.serial.SerialBlob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.uade.eccomerce.entity.ImagenProductos;
import com.uade.eccomerce.entity.Producto;
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

    @Transactional(rollbackFor = Throwable.class)
    public void agregarImagenesAProducto(Long idProducto, List<MultipartFile> archivos) throws ProductoNotFoundException, java.io.IOException, java.sql.SQLException {
        
        Optional<Producto> result = productoRepository.findById(idProducto);
        if (result.isEmpty()) {
            throw new ProductoNotFoundException();
        }
        Producto producto = result.get();

        // Iteramos sobre la lista de archivos (imagenes)
        for (MultipartFile archivo : archivos) {
            if (!archivo.isEmpty()) { // Validamos que no esté vacío
                ImagenProductos img = new ImagenProductos();
                img.setContenido(new SerialBlob(archivo.getBytes()));
                img.setProducto(producto); 
                imagenRepository.save(img);
            }
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public void eliminarImagen(Long idImagen) throws ImagenNotFoundException {
        Optional<ImagenProductos> result = imagenRepository.findById(idImagen);
        if (result.isEmpty()) {
            throw new ImagenNotFoundException();
        }

        imagenRepository.deleteById(idImagen);
    }
}
