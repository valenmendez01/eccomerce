package com.uade.eccomerce.controllers.categorias;

import com.uade.eccomerce.controllers.ApiResponse;
import com.uade.eccomerce.entity.Categoria;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/categorias")
public class CategoriaController {

    @GetMapping
    public ResponseEntity<ApiResponse<List<Categoria>>> getCategorias() {
        
        // Obtenemos los valores del Enum y los convertimos a una Lista
        List<Categoria> listaCategorias = Arrays.asList(Categoria.values());
        
        // Retornamos el ResponseEntity usando tu ApiResponse
        return ResponseEntity.ok(
            new ApiResponse<>("Categorias obtenidas exitosamente", listaCategorias)
        );
    }
}
