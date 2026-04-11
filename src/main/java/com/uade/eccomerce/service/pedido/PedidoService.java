package com.uade.eccomerce.service.pedido;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.uade.eccomerce.controllers.pedidos.PedidoResponse;
import com.uade.eccomerce.exceptions.usuarios.*;
import com.uade.eccomerce.exceptions.pedidos.PedidoIdInvalidoException;
import com.uade.eccomerce.exceptions.pedidos.PedidoNotFoundException;
import com.uade.eccomerce.exceptions.productos.*;
import com.uade.eccomerce.controllers.pedidos.PedidoRequest;


public interface PedidoService {

    PedidoResponse crearPedido(PedidoRequest request) throws UsuarioNotFoundException, ProductoNotFoundException, StockInsuficienteException;

    Page<PedidoResponse> obtenerTodosLosPedidos(PageRequest pageable) throws PedidoNotFoundException;

    Page<PedidoResponse> obtenerPedidosPorUsuario(Long idUsuario, PageRequest pageable) throws UsuarioNotFoundException, PedidoNotFoundException;

    PedidoResponse obtenerPedidoPorId(Long id) throws PedidoIdInvalidoException, PedidoNotFoundException;

    void eliminarPedido(Long id) throws PedidoIdInvalidoException, PedidoNotFoundException;

}
