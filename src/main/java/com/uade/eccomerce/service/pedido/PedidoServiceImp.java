package com.uade.eccomerce.service.pedido;

import com.uade.eccomerce.service.producto.ProductoService;
import java.sql.Date;
import java.util.Optional;

import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.eccomerce.controllers.pedidos.PedidoResponse;
import com.uade.eccomerce.controllers.pedidos.ItemRequest;
import com.uade.eccomerce.controllers.pedidos.PedidoRequest;
import com.uade.eccomerce.entity.DetallePedidos;
import com.uade.eccomerce.entity.Pedido;
import com.uade.eccomerce.entity.Producto;
import com.uade.eccomerce.entity.Usuario;
import com.uade.eccomerce.exceptions.pedidos.PedidoIdInvalidoException;
import com.uade.eccomerce.exceptions.pedidos.PedidoNotFoundException;
import com.uade.eccomerce.exceptions.productos.ProductoNotFoundException;
import com.uade.eccomerce.exceptions.productos.StockInsuficienteException;
import com.uade.eccomerce.exceptions.usuarios.UsuarioNotFoundException;
import com.uade.eccomerce.repository.PedidoRepository;
import com.uade.eccomerce.repository.ProductoRepository;
import com.uade.eccomerce.repository.UsuarioRepository;

@Service
public class PedidoServiceImp implements PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private ProductoService productoService;

    @Transactional(rollbackFor = Throwable.class)
    public PedidoResponse crearPedido(PedidoRequest request)
            throws UsuarioNotFoundException, ProductoNotFoundException, StockInsuficienteException {

        // 1. Obtenemos el email del usuario autenticado desde el contexto de Spring Security
        String emailLogueado = SecurityContextHolder.getContext().getAuthentication().getName();

        // 2. Buscamos al usuario en la base de datos usando su email
        Usuario usuario = usuarioRepository
            .findByEmail(emailLogueado)
            .orElseThrow(UsuarioNotFoundException::new);

        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setFechaPedido(new Date(System.currentTimeMillis()));

        Double total = 0.0;

        for (ItemRequest item : request.getItems()) {

            Producto producto = productoRepository
                .findById(item.getIdProducto())
                .orElseThrow(ProductoNotFoundException::new);

            if (!productoService.tieneStock(item.getIdProducto(), item.getCantidad())) {
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

    public Page<PedidoResponse> obtenerTodosLosPedidos(PageRequest pageable) throws PedidoNotFoundException {
        Page<Pedido> pedidos = pedidoRepository.findAll(pageable);

        if (pedidos.isEmpty()) {
            throw new PedidoNotFoundException();
        }

        return pedidos.map(this::convertirAResponse);
    }

    public Page<PedidoResponse> obtenerPedidosPorUsuario(Long idUsuario, PageRequest pageable) throws UsuarioNotFoundException, PedidoNotFoundException {
    
        // Validar si el usuario existe antes de buscar sus pedidos
        if (!usuarioRepository.existsById(idUsuario)) {
            throw new UsuarioNotFoundException();
        }

        // Realizar la búsqueda paginada
        Page<Pedido> pedidos = pedidoRepository.findByUsuarioIdUsuario(idUsuario, pageable);

        if (pedidos.isEmpty()) {
            throw new PedidoNotFoundException();
        }

        return pedidos.map(this::convertirAResponse);
    }

    public PedidoResponse obtenerPedidoPorId(Long id) throws PedidoIdInvalidoException, PedidoNotFoundException {
        // Validamos nulidad del ID
        if (id == null) {
            throw new PedidoIdInvalidoException();
        }

        Optional<Pedido> pedido = pedidoRepository.findById(id);

        // Validamos si se encontró el pedido
        if (!pedido.isPresent()) {
            throw new PedidoNotFoundException();
        }

        return convertirAResponse(pedido.get());
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
