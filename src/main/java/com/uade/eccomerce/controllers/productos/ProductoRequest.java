package com.uade.eccomerce.controllers.productos;

import com.uade.eccomerce.entity.Categoria;
import lombok.Data;

@Data
public class ProductoRequest {
    private String nombre;
    private String description;
    private Double precio;
    private Integer stock;
    private Integer descuento;
    private Categoria categoria;
    private Long idUsuario;
}