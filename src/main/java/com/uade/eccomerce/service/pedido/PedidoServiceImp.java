package com.uade.eccomerce.service.pedido;

import com.uade.eccomerce.service.producto.ProductoService;
import java.sql.Date;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

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
import com.uade.eccomerce.service.email.EmailService;

@Service
public class PedidoServiceImp implements PedidoService {

    private static final DateTimeFormatter FORMATO_FECHA =
        DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", Locale.forLanguageTag("es-AR"));

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private EmailService emailService;

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

    private Double calcularPrecioUnitarioPedido(Producto producto) {
        Double precio = producto.getPrecio() == null ? 0.0 : producto.getPrecio();
        Integer descuento = producto.getDescuento() == null ? 0 : producto.getDescuento();

        return (double) Math.round(precio * (1 - descuento / 100.0));
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
        Set<String> emailsVendedores = new HashSet<>();

        for (ItemRequest item : request.getItems()) {

            Producto producto = productoRepository
                .findById(item.getIdProducto())
                .orElseThrow(ProductoNotFoundException::new);

            if (!productoService.tieneStock(item.getIdProducto(), item.getCantidad())) {
                throw new StockInsuficienteException();
            }

            Double precioUnitario = calcularPrecioUnitarioPedido(producto);
            if (producto.getUsuario() != null && producto.getUsuario().getEmail() != null) {
                emailsVendedores.add(producto.getUsuario().getEmail());
            }

            DetallePedidos detalle = new DetallePedidos();
            detalle.setProducto(producto);
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(precioUnitario);

            producto.setStock(producto.getStock() - item.getCantidad());
            productoRepository.save(producto);

            total += precioUnitario * item.getCantidad();

            pedido.addDetalle(detalle);
        }

        pedido.setTotal(total);

        Pedido guardado = pedidoRepository.save(pedido);
        PedidoResponse response = convertirAResponse(guardado);
        enviarEmailsPedido(response, usuario, emailsVendedores);

        return response;
    }

    private void enviarEmailsPedido(PedidoResponse pedido, Usuario comprador, Set<String> emailsVendedores) {
        enviarEmailComprador(pedido, comprador);
        enviarEmailVendedores(pedido, comprador, emailsVendedores);
    }

    private void enviarEmailComprador(PedidoResponse pedido, Usuario comprador) {
        try {
            emailService.enviarEmailHtml(
                comprador.getEmail(),
                "Pedido confirmado #" + pedido.getIdPedido(),
                crearHtmlPedidoComprador(pedido, comprador));
        } catch (Exception e) {
            System.err.println("No se pudo enviar el email del pedido al comprador: " + e.getMessage());
        }
    }

    private void enviarEmailVendedores(PedidoResponse pedido, Usuario comprador, Set<String> emailsVendedores) {
        for (String emailVendedor : emailsVendedores) {
            try {
                emailService.enviarEmailHtml(
                    emailVendedor,
                    "Nueva venta en FIGULLECT - Pedido #" + pedido.getIdPedido(),
                    crearHtmlVentaVendedor(pedido, comprador));
            } catch (Exception e) {
                System.err.println("No se pudo enviar el email de venta al vendedor: " + e.getMessage());
            }
        }
    }

    private String crearHtmlPedidoComprador(PedidoResponse pedido, Usuario comprador) {
        String nombre = textoSeguro(comprador.getNombre());

        return crearHtmlBase(
            "Detalle del pedido",
            "Pedido #" + pedido.getIdPedido(),
            "<p style='font-size:16px;line-height:1.7;margin:0 0 18px;'>Hola " + nombre + ", tu pedido fue confirmado correctamente.</p>",
            "Información del pedido",
            "",
            pedido);
    }

    private String crearHtmlVentaVendedor(PedidoResponse pedido, Usuario comprador) {
        String compradorNombre = textoSeguro(comprador.getNombre() + " " + comprador.getApellido());
        String compradorEmail = textoSeguro(comprador.getEmail());

        return crearHtmlBase(
            "Detalle de la venta",
            "Venta #" + pedido.getIdPedido(),
            "<p style='font-size:16px;line-height:1.7;margin:0 0 18px;'>Recibiste una nueva venta en FIGULLECT.</p>",
            "Información de la venta",
            crearDatoResumen("Comprador", compradorNombre)
                + crearDatoResumen("Email", compradorEmail),
            pedido);
    }

    private String crearHtmlBase(
            String etiqueta,
            String titulo,
            String introduccion,
            String tituloResumen,
            String datosResumenExtra,
            PedidoResponse pedido) {
        return "<div style='margin:0;padding:32px;background:#f7f5ef;font-family:Arial,sans-serif;color:#142b10;'>"
            + "<div style='max-width:900px;margin:0 auto;background:#fffdf8;border:1px solid #ead8bb;border-radius:18px;overflow:hidden;'>"
            + "<div style='background:#142b10;padding:24px 28px;text-align:center;'>"
            + "<img src='cid:logoFigulect' alt='FIGULLECT' style='width:190px;max-width:80%;height:auto;' />"
            + "</div>"
            + "<div style='padding:28px 32px;border-bottom:1px solid #ead8bb;'>"
            + "<p style='margin:0 0 8px;color:#caa56e;font-size:13px;letter-spacing:5px;text-transform:uppercase;font-weight:bold;'>"
            + etiqueta + "</p>"
            + "<h1 style='margin:0;font-size:32px;line-height:1.15;color:#142b10;'>" + titulo + "</h1>"
            + "</div>"
            + "<div style='padding:30px 32px;'>"
            + introduccion
            + "<table style='width:100%;border-collapse:collapse;border:1px solid #ead8bb;border-radius:12px;overflow:hidden;'>"
            + "<thead><tr style='background:#f1f4f8;color:#91a0b8;text-transform:uppercase;font-size:13px;'>"
            + "<th style='text-align:left;padding:14px;'>Producto</th>"
            + "<th style='text-align:center;padding:14px;'>Cantidad</th>"
            + "<th style='text-align:left;padding:14px;'>Precio unitario</th>"
            + "<th style='text-align:left;padding:14px;'>Subtotal</th>"
            + "</tr></thead>"
            + "<tbody>" + crearFilasProductos(pedido) + "</tbody>"
            + "</table>"
            + crearResumenPedido(tituloResumen, datosResumenExtra, pedido)
            + "<p style='font-size:15px;line-height:1.7;margin:28px 0 0;color:#5f6f5b;'>Equipo FIGULLECT</p>"
            + "</div>"
            + "</div>"
            + "</div>";
    }

    private String crearFilasProductos(PedidoResponse pedido) {
        if (pedido.getItems() == null || pedido.getItems().isEmpty()) {
            return "<tr><td colspan='4' style='padding:18px;color:#7b8875;'>No hay productos asociados a este pedido.</td></tr>";
        }

        return pedido.getItems().stream()
            .map(this::crearFilaProducto)
            .reduce("", String::concat);
    }

    private String crearFilaProducto(ItemPedidoResponse item) {
        return "<tr style='border-top:1px solid #edf0f3;'>"
            + "<td style='padding:16px;font-size:16px;font-weight:bold;color:#142b10;'>" + textoSeguro(item.getNombreProducto()) + "</td>"
            + "<td style='padding:16px;text-align:center;font-size:16px;color:#142b10;'>" + item.getCantidad() + "</td>"
            + "<td style='padding:16px;font-size:16px;color:#142b10;'>" + formatearPrecio(item.getPrecioUnitario()) + "</td>"
            + "<td style='padding:16px;font-size:16px;font-weight:bold;color:#142b10;'>" + formatearPrecio(item.getSubtotal()) + "</td>"
            + "</tr>";
    }

    private String crearResumenPedido(String tituloResumen, String datosResumenExtra, PedidoResponse pedido) {
        return "<div style='margin-top:24px;border:1px solid #ead8bb;border-radius:12px;padding:22px;background:#fffdf8;'>"
            + "<h2 style='margin:0 0 18px;font-size:18px;letter-spacing:3px;text-transform:uppercase;color:#142b10;'>"
            + tituloResumen + "</h2>"
            + datosResumenExtra
            + crearDatoResumen("Productos", String.valueOf(cantidadProductos(pedido)))
            + crearDatoResumen("Unidades", String.valueOf(cantidadUnidades(pedido)))
            + crearDatoResumen("Fecha de compra", formatearFecha(pedido.getFechaPedido()))
            + "<div style='height:1px;background:#ead8bb;margin:18px 0;'></div>"
            + "<p style='margin:0 0 8px;color:#91a0b8;font-size:14px;letter-spacing:4px;text-transform:uppercase;font-weight:bold;'>Total</p>"
            + "<p style='margin:0;font-size:30px;font-weight:bold;color:#142b10;'>" + formatearPrecio(pedido.getTotal()) + "</p>"
            + "</div>";
    }

    private String crearDatoResumen(String titulo, String valor) {
        return "<div style='background:#f3efe7;border-radius:10px;padding:16px;margin-bottom:12px;'>"
            + "<p style='margin:0 0 8px;color:#91a0b8;font-size:14px;letter-spacing:4px;text-transform:uppercase;font-weight:bold;'>"
            + titulo + "</p>"
            + "<p style='margin:0;font-size:18px;font-weight:bold;color:#142b10;'>" + valor + "</p>"
            + "</div>";
    }

    private int cantidadProductos(PedidoResponse pedido) {
        return pedido.getItems() == null ? 0 : pedido.getItems().size();
    }

    private int cantidadUnidades(PedidoResponse pedido) {
        return pedido.getItems() == null
            ? 0
            : pedido.getItems().stream()
                .mapToInt(item -> item.getCantidad() == null ? 0 : item.getCantidad())
                .sum();
    }

    private String formatearFecha(Date fecha) {
        return fecha == null ? "" : fecha.toLocalDate().format(FORMATO_FECHA);
    }

    private String formatearPrecio(Double precio) {
        long valor = Math.round(precio == null ? 0 : precio);
        return "$" + String.format(Locale.US, "%,d", valor).replace(",", ".");
    }

    private String textoSeguro(String texto) {
        return HtmlUtils.htmlEscape(texto == null ? "" : texto);
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

        if (!usuarioRepository.existsById(idUsuario)) {
            throw new UsuarioNotFoundException();
        }

        Page<Pedido> pedidos = pedidoRepository.findByUsuarioIdUsuario(idUsuario, pageable);

        if (pedidos.isEmpty()) {
            throw new PedidoNotFoundException();
        }

        return pedidos.map(this::convertirAResponse);
    }

    public PedidoResponse obtenerPedidoPorId(Long id, String emailUsuario, boolean esVendedor) throws PedidoIdInvalidoException, PedidoNotFoundException {
        if (id == null) {
            throw new PedidoIdInvalidoException();
        }

        Optional<Pedido> pedido = pedidoRepository.findById(id);

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
