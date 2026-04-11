package com.uade.eccomerce.controllers.imagenes;

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

@RestController
@RequestMapping("/productos/{idProducto}/imagenes")
public class ImagenController {

    @Autowired
    private ImagenServiceImp imagenService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> subirImagen(
            @PathVariable Long idProducto,
            @RequestParam("archivo") MultipartFile archivo) throws ProductoNotFoundException, java.io.IOException, java.sql.SQLException {
        imagenService.agregarImagenAProducto(idProducto, archivo);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{idImagen}")
    public ResponseEntity<Void> eliminarImagen(@PathVariable Long idImagen) throws ImagenNotFoundException {
        imagenService.eliminarImagen(idImagen);
        return ResponseEntity.noContent().build();
    }
}
