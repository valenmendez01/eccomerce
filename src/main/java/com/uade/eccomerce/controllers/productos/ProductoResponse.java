package com.uade.eccomerce.controllers.productos;

import com.uade.eccomerce.controllers.imagenes.ImagenResponse;
import com.uade.eccomerce.entity.Categoria;
import com.uade.eccomerce.entity.Seleccion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoResponse {
    private Long idProducto;
    private String nombre;
    private String description;
    private Double precio;
    private Integer stock;
    private Boolean disponible;
    private Integer descuento;
    private Boolean destacado;
    private Categoria categoria;
    private Seleccion seleccion;
    private Boolean activo;
    
    private Long idUsuario;
    private String nombreUsuario;

    private List<ImagenResponse> imagenes;
}
