package com.uade.eccomerce.controllers.pedidos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.uade.eccomerce.controllers.ApiResponse;
import com.uade.eccomerce.controllers.config.PaginacionUtils;
import com.uade.eccomerce.exceptions.pedidos.PedidoIdInvalidoException;
import com.uade.eccomerce.exceptions.pedidos.PedidoNotFoundException;
import com.uade.eccomerce.exceptions.productos.ProductoNotFoundException;
import com.uade.eccomerce.exceptions.productos.StockInsuficienteException;
import com.uade.eccomerce.exceptions.usuarios.UsuarioNotFoundException;
import com.uade.eccomerce.service.pedido.PedidoService;

/**
 * Controller para gestionar los pedidos del ecommerce.
 * Permite a los compradores generar pedidos y a los vendedores visualizar las ventas.
 * * Endpoints:
 * - crearPedido() - POST /pedidos - Genera un nuevo pedido, asocia los items correspondientes y descuenta el stock de los productos
 * - obtenerVentasDelVendedor() - GET /pedidos - Devuelve únicamente las ventas del vendedor autenticado
 * - obtenerPedidosPorUsuario() - GET /pedidos/usuario/{idUsuario} - Devuelve el historial de pedidos de un usuario específico (soporta paginación)
 * - obtenerPedidoPorId() - GET /pedidos/{id} - Devuelve el detalle completo de un pedido por su ID
 * - eliminarPedido() - DELETE /pedidos/{id} - Elimina un pedido
 */

@RestController
@RequestMapping("/pedidos")
public class PedidosControllers  {
    

    @Autowired
    private PedidoService pedidoService;

    private boolean esVendedor(Authentication authentication) {
        return authentication != null
                && authentication.getAuthorities().stream()
                        .anyMatch(autoridad -> "VENDEDOR".equals(autoridad.getAuthority()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PedidoResponse>> crearPedido(@RequestBody PedidoRequest request, Authentication authentication)
            throws UsuarioNotFoundException, ProductoNotFoundException, StockInsuficienteException {
        PedidoResponse response = pedidoService.crearPedido(request, authentication == null ? null : authentication.getName());
        return ResponseEntity.ok(new ApiResponse<>("Pedido creado con éxito", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<PedidoResponse>>> obtenerVentasDelVendedor(
            Authentication authentication,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) 
            throws UsuarioNotFoundException {
        return ResponseEntity.ok(new ApiResponse<>(
                "Ventas del vendedor obtenidas",
                pedidoService.obtenerVentasDelVendedor(
                        authentication == null ? null : authentication.getName(),
                        PaginacionUtils.crear(page, size))));
    }

    @GetMapping("/comprador")
    public ResponseEntity<ApiResponse<Page<PedidoResponse>>> obtenerPedidosDelComprador(
            Authentication authentication,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size)
            throws UsuarioNotFoundException {
        PageRequest pageable = PaginacionUtils.crear(page, size);

        return ResponseEntity.ok(new ApiResponse<>("Pedidos del usuario autenticado obtenidos",
                pedidoService.obtenerPedidosDelComprador(authentication == null ? null : authentication.getName(), pageable)));
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<ApiResponse<Page<PedidoResponse>>> obtenerPedidosPorUsuario(
            @PathVariable Long idUsuario,
            Authentication authentication,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) 
            throws UsuarioNotFoundException, PedidoNotFoundException {
        
        if (page == null || size == null) {
            return ResponseEntity.ok(new ApiResponse<>("Historial de pedidos del usuario obtenido", pedidoService.obtenerPedidosPorUsuario(idUsuario, authentication == null ? null : authentication.getName(), PaginacionUtils.crear(page, size))));
        }
        
        return ResponseEntity.ok(new ApiResponse<>("Historial de pedidos del usuario obtenido", pedidoService.obtenerPedidosPorUsuario(idUsuario, authentication == null ? null : authentication.getName(), PaginacionUtils.crear(page, size))));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PedidoResponse>> obtenerPedidoPorId(@PathVariable Long id, Authentication authentication) throws PedidoIdInvalidoException, PedidoNotFoundException {
        return ResponseEntity.ok(new ApiResponse<>("Detalle del pedido obtenido", pedidoService.obtenerPedidoPorId(id, authentication == null ? null : authentication.getName(), esVendedor(authentication))));
    }

}
