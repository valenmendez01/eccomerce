package com.uade.eccomerce.controllers.selecciones;

import com.uade.eccomerce.controllers.ApiResponse;
import com.uade.eccomerce.entity.Seleccion;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/selecciones")
public class SeleccionController {

    @GetMapping
    public ResponseEntity<ApiResponse<List<Seleccion>>> getSelecciones() {
        List<Seleccion> listaSelecciones = Arrays.asList(Seleccion.values());
        
        return ResponseEntity.ok(
            new ApiResponse<>("Selecciones obtenidas exitosamente", listaSelecciones)
        );
    }
}