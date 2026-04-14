package com.uade.eccomerce.controllers.imagenes;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.uade.eccomerce.exceptions.imagenes.ImagenNotFoundException;
import com.uade.eccomerce.exceptions.productos.ProductoNotFoundException;
import com.uade.eccomerce.service.imagen.ImagenServiceImp;

/**
 * Controller para la gestión de archivos e imágenes de los productos.
 * Permite asociar imágenes a los artículos del catálogo mediante subida de archivos (multipart).
 * * Endpoints:
 * - subirImagenes() - POST /productos/{idProducto}/imagenes - Recibe una o múltiples imágenes (archivos) y las guarda asociadas al producto indicado
 * - eliminarImagen() - DELETE /productos/{idProducto}/imagenes/{idImagen} - Elimina de la base de datos una imagen específica por su ID
 */

@RestController
@RequestMapping("/productos/{idProducto}/imagenes")
public class ImagenController {

    @Autowired
    private ImagenServiceImp imagenService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> subirImagenes(
            @PathVariable Long idProducto,
            @RequestParam("archivos") List<MultipartFile> archivos) throws ProductoNotFoundException, java.io.IOException, java.sql.SQLException {
        imagenService.agregarImagenesAProducto(idProducto, archivos);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{idImagen}")
    public ResponseEntity<Void> eliminarImagen(
            @PathVariable Long idProducto, 
            @PathVariable Long idImagen) throws ImagenNotFoundException {
        imagenService.eliminarImagen(idImagen);
        return ResponseEntity.noContent().build();
    }
}
