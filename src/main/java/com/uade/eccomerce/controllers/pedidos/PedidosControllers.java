package com.uade.eccomerce.controllers.pedidos;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.uade.eccomerce.exceptions.pedidos.PedidoIdInvalidoException;
import com.uade.eccomerce.exceptions.pedidos.PedidoNotFoundException;
import com.uade.eccomerce.exceptions.productos.ProductoNotFoundException;
import com.uade.eccomerce.exceptions.productos.StockInsuficienteException;
import com.uade.eccomerce.exceptions.usuarios.UsuarioNotFoundException;
import com.uade.eccomerce.service.pedido.PedidoService;


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
    public List<PedidoResponse> obtenerTodosLosPedidos() throws PedidoNotFoundException {
        return pedidoService.obtenerTodosLosPedidos();
    }

    @GetMapping("/{id}")
    public PedidoResponse obtenerPedidoPorId(@PathVariable Long id) throws PedidoIdInvalidoException, PedidoNotFoundException {
        return pedidoService.obtenerPedidoPorId(id);
    }

    // @PutMapping("/{id}")
    // public PedidoResponse actualizarPedido(@PathVariable Long id, @RequestBody PedidoRequest request) {
    //     return pedidoService.actualizarPedido(id, request);
    // }

    @DeleteMapping("/{id}")
    public void eliminarPedido(@PathVariable Long id) throws PedidoIdInvalidoException, PedidoNotFoundException {
        pedidoService.eliminarPedido(id);
    }

}
