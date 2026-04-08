package com.uade.eccomerce.service.pedido;

import java.util.List;

import com.uade.eccomerce.controllers.pedidos.PedidoResponse;
import com.uade.eccomerce.controllers.pedidos.PedidoRequest;


public interface PedidoService {

    PedidoResponse crearPedido(PedidoRequest request);

    List<PedidoResponse> obtenerTodosLosPedidos();

    PedidoResponse obtenerPedidoPorId(Long id);

    PedidoResponse actualizarPedido(Long id, PedidoRequest request);

    void eliminarPedido(Long id);

}
