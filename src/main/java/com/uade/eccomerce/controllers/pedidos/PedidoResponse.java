package com.uade.eccomerce.controllers.pedidos;

import java.sql.Date;
import lombok.Data;

@Data

public class PedidoResponse {
    
    private Long idPedido;
    private Date fechaPedido;
    private Double total;
    private Long idUsuario;

}
