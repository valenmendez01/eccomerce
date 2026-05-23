package com.uade.eccomerce.service.pedido;

import com.uade.eccomerce.service.producto.ProductoService;
import java.sql.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.eccomerce.controllers.pedidos.PedidoResponse;
import com.uade.eccomerce.controllers.usuarios.UsuarioResponse;
import com.uade.eccomerce.controllers.pedidos.ItemRequest;
import com.uade.eccomerce.controllers.pedidos.ItemPedidoResponse;
import com.uade.eccomerce.controllers.pedidos.PedidoRequest;
import com.uade.eccomerce.entity.DetallePedidos;
import com.uade.eccomerce.entity.Pedido;
import com.uade.eccomerce.entity.Producto;
import com.uade.eccomerce.entity.Usuario;
import com.uade.eccomerce.exceptions.AccesoPedidoNoAutorizadoException;
import com.uade.eccomerce.exceptions.SolicitudInvalidaException;
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

    private Usuario obtenerUsuarioPorEmail(String email) throws UsuarioNotFoundException {
        if (email == null || email.isBlank()) {
            throw new UsuarioNotFoundException();
        }

        return usuarioRepository.findByEmail(email)
            .orElseThrow(UsuarioNotFoundException::new);
    }

    private void validarPedido(PedidoRequest request) {
        if (request == null || request.getItems() == null || request.getItems().isEmpty()) {
            throw new SolicitudInvalidaException("El pedido debe tener al menos un producto.");
        }

        for (ItemRequest item : request.getItems()) {
            if (item == null || item.getIdProducto() == null) {
                throw new SolicitudInvalidaException("Cada item debe tener un producto valido.");
            }

            if (item.getCantidad() == null || item.getCantidad() <= 0) {
                throw new SolicitudInvalidaException("La cantidad de cada item debe ser mayor a cero.");
            }
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public PedidoResponse crearPedido(PedidoRequest request, String emailComprador)
            throws UsuarioNotFoundException, ProductoNotFoundException, StockInsuficienteException {

        validarPedido(request);
        Usuario usuario = obtenerUsuarioPorEmail(emailComprador);

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

    @Transactional(readOnly = true)
    public Page<PedidoResponse> obtenerPedidosDelComprador(String emailComprador, PageRequest pageable) throws UsuarioNotFoundException {
        Usuario usuario = obtenerUsuarioPorEmail(emailComprador);

        return pedidoRepository
            .findByUsuarioIdUsuario(usuario.getIdUsuario(), pageable)
            .map(this::convertirAResponse);
    }

    @Transactional(readOnly = true)
    public Page<PedidoResponse> obtenerVentasDelVendedor(String emailVendedor, PageRequest pageable) throws UsuarioNotFoundException {
        Usuario vendedor = obtenerUsuarioPorEmail(emailVendedor);

        return pedidoRepository
            .findDistinctByDetallePedidosProductoUsuarioEmail(vendedor.getEmail(), pageable)
            .map(pedido -> convertirAResponse(pedido, vendedor.getEmail()));
    }

    public Page<PedidoResponse> obtenerPedidosPorUsuario(Long idUsuario, String emailComprador, PageRequest pageable) throws UsuarioNotFoundException, PedidoNotFoundException {
    
        Usuario usuario = obtenerUsuarioPorEmail(emailComprador);
        if (!usuario.getIdUsuario().equals(idUsuario)) {
            throw new AccesoPedidoNoAutorizadoException();
        }

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

    public PedidoResponse obtenerPedidoPorId(Long id, String emailUsuario, boolean esVendedor) throws PedidoIdInvalidoException, PedidoNotFoundException {
        // Validamos nulidad del ID
        if (id == null) {
            throw new PedidoIdInvalidoException();
        }

        Optional<Pedido> pedido = pedidoRepository.findById(id);

        // Validamos si se encontró el pedido
        if (!pedido.isPresent()) {
            throw new PedidoNotFoundException();
        }

        Pedido pedidoEncontrado = pedido.get();
        if (!esVendedor && !pedidoEncontrado.getUsuario().getEmail().equals(emailUsuario)) {
            throw new AccesoPedidoNoAutorizadoException();
        }

        return convertirAResponse(pedidoEncontrado);
    }

    private PedidoResponse convertirAResponse(Pedido pedido) {
        return convertirAResponse(pedido, null);
    }

    private PedidoResponse convertirAResponse(Pedido pedido, String emailVendedor) {
        List<DetallePedidos> detalles = pedido.getDetallePedidos() == null
            ? List.of()
            : pedido.getDetallePedidos();

        if (emailVendedor != null) {
            detalles = detalles.stream()
                .filter(detalle -> detalle.getProducto() != null
                    && detalle.getProducto().getUsuario() != null
                    && emailVendedor.equals(detalle.getProducto().getUsuario().getEmail()))
                .toList();
        }

        List<ItemPedidoResponse> items = detalles.stream()
            .map(detalle -> ItemPedidoResponse.builder()
                .idProducto(detalle.getProducto().getIdProducto())
                .nombreProducto(detalle.getProducto().getNombre())
                .cantidad(detalle.getCantidad())
                .precioUnitario(detalle.getPrecioUnitario())
                .subtotal(detalle.getPrecioUnitario() * detalle.getCantidad())
                .build())
            .toList();

        Double total = emailVendedor == null
            ? pedido.getTotal()
            : items.stream().mapToDouble(ItemPedidoResponse::getSubtotal).sum();

        PedidoResponse response = new PedidoResponse();

        response.setIdPedido(pedido.getIdPedido());
        response.setFechaPedido(pedido.getFechaPedido());
        response.setTotal(total);
        response.setIdUsuario(pedido.getUsuario().getIdUsuario());
        response.setComprador(UsuarioResponse.from(pedido.getUsuario()));
        response.setItems(items);

        return response;
    }
}
