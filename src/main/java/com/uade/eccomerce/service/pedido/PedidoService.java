package com.uade.eccomerce.service.pedido;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.uade.eccomerce.controllers.pedidos.PedidoResponse;
import com.uade.eccomerce.exceptions.usuarios.*;
import com.uade.eccomerce.exceptions.pedidos.PedidoIdInvalidoException;
import com.uade.eccomerce.exceptions.pedidos.PedidoNotFoundException;
import com.uade.eccomerce.exceptions.productos.*;
import com.uade.eccomerce.exceptions.AccesoPedidoNoAutorizadoException;
import com.uade.eccomerce.controllers.pedidos.PedidoRequest;


public interface PedidoService {

    PedidoResponse crearPedido(PedidoRequest request, String emailComprador) throws UsuarioNotFoundException, ProductoNotFoundException, StockInsuficienteException;

    Page<PedidoResponse> obtenerPedidosDelComprador(String emailComprador, PageRequest pageable) throws UsuarioNotFoundException;

    Page<PedidoResponse> obtenerVentasDelVendedor(String emailVendedor, PageRequest pageable) throws UsuarioNotFoundException;

    Page<PedidoResponse> obtenerPedidosPorUsuario(Long idUsuario, String emailComprador, PageRequest pageable) throws UsuarioNotFoundException, PedidoNotFoundException, AccesoPedidoNoAutorizadoException;

    PedidoResponse obtenerPedidoPorId(Long id, String emailUsuario, boolean esVendedor) throws PedidoIdInvalidoException, PedidoNotFoundException, AccesoPedidoNoAutorizadoException;

}
