package com.uade.eccomerce.controllers.productos;

import com.uade.eccomerce.entity.Categoria;
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
    private Integer descuento;
    private Categoria categoria;
    private Boolean activo;
    
    private Long idUsuario;
    private String nombreUsuario;

    private List<String> urlsImagenes;
}
