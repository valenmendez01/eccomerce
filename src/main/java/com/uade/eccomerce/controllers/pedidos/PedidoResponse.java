package com.uade.eccomerce.controllers.pedidos;

import java.sql.Date;
import java.util.List;

import com.uade.eccomerce.controllers.usuarios.UsuarioResponse;

import lombok.Data;

@Data

public class PedidoResponse {
    
    private Long idPedido;
    private Date fechaPedido;
    private Double total;
    private Long idUsuario;
    private UsuarioResponse comprador;
    private List<ItemPedidoResponse> items;

}
