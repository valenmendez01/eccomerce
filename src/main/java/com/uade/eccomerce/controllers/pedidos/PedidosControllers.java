package com.uade.eccomerce.controllers.pedidos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
 * - obtenerTodosLosPedidos() - GET /pedidos - Devuelve el listado completo de todos los pedidos del sistema (soporta paginación)
 * - obtenerPedidosPorUsuario() - GET /pedidos/usuario/{idUsuario} - Devuelve el historial de pedidos de un usuario específico (soporta paginación)
 * - obtenerPedidoPorId() - GET /pedidos/{id} - Devuelve el detalle completo de un pedido por su ID
 * - eliminarPedido() - DELETE /pedidos/{id} - Elimina un pedido
 */

@RestController
@RequestMapping("/pedidos")
public class PedidosControllers  {
    

    @Autowired
    private PedidoService pedidoService;

    @PostMapping
    public PedidoResponse crearPedido(@RequestBody PedidoRequest request)
            throws UsuarioNotFoundException, ProductoNotFoundException, StockInsuficienteException {

        return pedidoService.crearPedido(request);
    }

    @GetMapping
    public ResponseEntity<Page<PedidoResponse>> obtenerTodosLosPedidos(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) 
            throws PedidoNotFoundException {
        
        if (page == null || size == null) {
            return ResponseEntity.ok(pedidoService.obtenerTodosLosPedidos(PageRequest.of(0, Integer.MAX_VALUE)));
        }
        
        return ResponseEntity.ok(pedidoService.obtenerTodosLosPedidos(PageRequest.of(page, size)));
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<Page<PedidoResponse>> obtenerPedidosPorUsuario(
            @PathVariable Long idUsuario,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) 
            throws UsuarioNotFoundException, PedidoNotFoundException {
        
        if (page == null || size == null) {
            return ResponseEntity.ok(pedidoService.obtenerPedidosPorUsuario(idUsuario, PageRequest.of(0, Integer.MAX_VALUE)));
        }
        
        return ResponseEntity.ok(pedidoService.obtenerPedidosPorUsuario(idUsuario, PageRequest.of(page, size)));
    }

    @GetMapping("/{id}")
    public PedidoResponse obtenerPedidoPorId(@PathVariable Long id) throws PedidoIdInvalidoException, PedidoNotFoundException {
        return pedidoService.obtenerPedidoPorId(id);
    }

    @DeleteMapping("/{id}")
    public void eliminarPedido(@PathVariable Long id) throws PedidoIdInvalidoException, PedidoNotFoundException {
        pedidoService.eliminarPedido(id);
    }

}
