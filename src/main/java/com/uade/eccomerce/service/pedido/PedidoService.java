package com.uade.eccomerce.service.pedido;

import java.util.List;

import com.uade.eccomerce.controllers.pedidos.PedidoResponse;
import com.uade.eccomerce.exceptions.usuarios.*;
import com.uade.eccomerce.exceptions.pedidos.PedidoIdInvalidoException;
import com.uade.eccomerce.exceptions.pedidos.PedidoNotFoundException;
import com.uade.eccomerce.exceptions.productos.*;
import com.uade.eccomerce.controllers.pedidos.PedidoRequest;


public interface PedidoService {

    PedidoResponse crearPedido(PedidoRequest request)
        throws UsuarioNotFoundException,
               ProductoNotFoundException,
               StockInsuficienteException;

    List<PedidoResponse> obtenerTodosLosPedidos() throws PedidoNotFoundException;

    PedidoResponse obtenerPedidoPorId(Long id) throws PedidoIdInvalidoException, PedidoNotFoundException;

    void eliminarPedido(Long id) throws PedidoIdInvalidoException, PedidoNotFoundException;

}
