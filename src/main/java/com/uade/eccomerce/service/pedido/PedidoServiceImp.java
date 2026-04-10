package com.uade.eccomerce.service.pedido;


import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uade.eccomerce.controllers.pedidos.PedidoResponse;
import com.uade.eccomerce.controllers.pedidos.ItemRequest;
import com.uade.eccomerce.controllers.pedidos.PedidoRequest;
import com.uade.eccomerce.entity.DetallePedidos;
import com.uade.eccomerce.entity.Pedido;
import com.uade.eccomerce.entity.Producto;
import com.uade.eccomerce.entity.Usuario;
import com.uade.eccomerce.exceptions.productos.ProductoNotFoundException;
import com.uade.eccomerce.exceptions.productos.StockInsuficienteException;
import com.uade.eccomerce.exceptions.usuarios.UsuarioNotFoundException;
import com.uade.eccomerce.repository.PedidoRepository;
import com.uade.eccomerce.repository.ProductoRepository;
import com.uade.eccomerce.repository.UsuarioRepository;

import jakarta.transaction.Transactional;

@Service
public class PedidoServiceImp implements PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Override
    @Transactional
    public PedidoResponse crearPedido(PedidoRequest request)
            throws UsuarioNotFoundException, ProductoNotFoundException, StockInsuficienteException {

        Usuario usuario = usuarioRepository
                .findById(request.getIdUsuario())
                .orElseThrow(UsuarioNotFoundException::new);

        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setFechaPedido(new Date(System.currentTimeMillis()));

        Double total = 0.0;

        for (ItemRequest item : request.getItems()) {

            Producto producto = productoRepository
                    .findById(item.getIdProducto())
                    .orElseThrow(ProductoNotFoundException::new);

            if (producto.getStock() < item.getCantidad()) {
                throw new StockInsuficienteException();
            }

            DetallePedidos detalle = new DetallePedidos();
            detalle.setProducto(producto);
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(producto.getPrecio());

            producto.setStock(producto.getStock() - item.getCantidad());
            productoRepository.save(producto);

            total += producto.getPrecio() * item.getCantidad();

            pedido.addDetalle(detalle);
        }

        pedido.setTotal(total);

        Pedido guardado = pedidoRepository.save(pedido);

        return convertirAResponse(guardado);
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

    // @Override
    // public PedidoResponse actualizarPedido(Long id, PedidoRequest request) {

    //     Pedido pedido = pedidoRepository.findById(id).orElse(null);

    //     if (pedido == null) {
    //         return null;
    //     }

    //     Usuario usuario = usuarioRepository.findById(request.getIdUsuario()).orElse(null);

    //     pedido.setUsuario(usuario);
    //     pedido.setTotal(request.getTotal());

    //     Pedido pedidoActualizado = pedidoRepository.save(pedido);

    //     return convertirAResponse(pedidoActualizado);
    // }

    @Override
    public void eliminarPedido(Long id) {
        pedidoRepository.deleteById(id);
    }

    private PedidoResponse convertirAResponse(Pedido pedido) {

        PedidoResponse response = new PedidoResponse();

        response.setIdPedido(pedido.getIdPedido());
        response.setFechaPedido(pedido.getFechaPedido());
        response.setTotal(pedido.getTotal());
        response.setIdUsuario(pedido.getUsuario().getIdUsuario());

        return response;
    }
}
