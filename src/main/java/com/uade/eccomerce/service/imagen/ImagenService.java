package com.uade.eccomerce.service.imagen;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.uade.eccomerce.exceptions.imagenes.ImagenNotFoundException;
import com.uade.eccomerce.exceptions.productos.ProductoNotFoundException;

public interface ImagenService {
    void agregarImagenesAProducto(Long idProducto, List<MultipartFile> archivos) throws ProductoNotFoundException, java.io.IOException, java.sql.SQLException;

    void eliminarImagen(Long idImagen) throws ImagenNotFoundException;
}
