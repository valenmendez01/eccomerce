package com.uade.eccomerce.controllers.pedidos;

import java.util.List;
import lombok.Data;

@Data

public class PedidoRequest {

    private Long idUsuario;
    private List<ItemRequest> items;

}
