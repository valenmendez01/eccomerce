package com.uade.eccomerce.service.pedido;


import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uade.eccomerce.controllers.pedidos.PedidoResponse;
import com.uade.eccomerce.controllers.pedidos.PedidoRequest;
import com.uade.eccomerce.entity.Pedido;
import com.uade.eccomerce.entity.Usuario;
import com.uade.eccomerce.repository.PedidoRepository;
import com.uade.eccomerce.repository.UsuarioRepository;

@Service
public class PedidoServiceImp implements PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public PedidoResponse crearPedido(PedidoRequest request) {

        Usuario usuario = usuarioRepository.findById(request.getIdUsuario()).orElse(null);

        Pedido pedido = new Pedido();
        pedido.setFecha_pedido(new Date(System.currentTimeMillis()));
        pedido.setTotal(request.getTotal());
        pedido.setUsuario(usuario);

        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        return convertirAResponse(pedidoGuardado);
    }

    @Override
    public List<PedidoResponse> obtenerTodosLosPedidos() {

        return pedidoRepository.findAll()
                .stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PedidoResponse obtenerPedidoPorId(Long id) {

        Pedido pedido = pedidoRepository.findById(id).orElse(null);

        if (pedido == null) {
            return null;
        }

        return convertirAResponse(pedido);
    }

    @Override
    public PedidoResponse actualizarPedido(Long id, PedidoRequest request) {

        Pedido pedido = pedidoRepository.findById(id).orElse(null);

        if (pedido == null) {
            return null;
        }

        Usuario usuario = usuarioRepository.findById(request.getIdUsuario()).orElse(null);

        pedido.setUsuario(usuario);
        pedido.setTotal(request.getTotal());

        Pedido pedidoActualizado = pedidoRepository.save(pedido);

        return convertirAResponse(pedidoActualizado);
    }

    @Override
    public void eliminarPedido(Long id) {
        pedidoRepository.deleteById(id);
    }

    private PedidoResponse convertirAResponse(Pedido pedido) {

        PedidoResponse response = new PedidoResponse();

        response.setIdPedido(pedido.getId_pedido());
        response.setFechaPedido(pedido.getFecha_pedido());
        response.setTotal(pedido.getTotal());
        response.setIdUsuario(pedido.getUsuario().getId_usuario());

        return response;
    }
}
