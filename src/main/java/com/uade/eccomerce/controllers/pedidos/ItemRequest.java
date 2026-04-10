package com.uade.eccomerce.controllers.pedidos;

import lombok.Data;

@Data 
public class ItemRequest {

    private Long idProducto;
    private Integer cantidad;
    
}
