package com.uade.eccomerce.controllers.productos;

import com.uade.eccomerce.entity.Categoria;
import com.uade.eccomerce.entity.Seleccion;

import lombok.Data;

@Data
public class ProductoRequest {
    private String nombre;
    private String description;
    private Double precio;
    private Integer stock;
    private Integer descuento;
    private Boolean destacado;
    private Categoria categoria;
    private Seleccion seleccion;
    private Boolean activo;
}
