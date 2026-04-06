package com.uade.eccomerce.controllers.productos;

import com.uade.eccomerce.entity.Categoria;
import lombok.Data;
import java.util.List;

@Data
public class ProductoRequest {
    private String nombre;
    private String description;
    private Double precio;
    private Integer stock;
    private Integer descuento;
    private Categoria categoria; // El Enum
    private Long idUsuario; // Del vendedor
    private List<String> urlsImagenes; // Lista de URLs de las fotos
}