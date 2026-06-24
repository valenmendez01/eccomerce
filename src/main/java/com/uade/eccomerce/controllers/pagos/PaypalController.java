package com.uade.eccomerce.controllers.pagos;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uade.eccomerce.controllers.ApiResponse;
import com.uade.eccomerce.controllers.pedidos.PedidoRequest;
import com.uade.eccomerce.controllers.pedidos.PedidoResponse;
import com.uade.eccomerce.exceptions.productos.ProductoNotFoundException;
import com.uade.eccomerce.exceptions.productos.StockInsuficienteException;
import com.uade.eccomerce.exceptions.usuarios.UsuarioNotFoundException;
import com.uade.eccomerce.service.pago.PaypalService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/pagos/paypal")
@RequiredArgsConstructor
public class PaypalController {

    private final PaypalService paypalService;

    @PostMapping("/crear-orden")
    public PaypalOrdenResponse crearOrden(@RequestBody PaypalCrearOrdenRequest request) {
        return paypalService.crearOrden(request);
    }

    @PostMapping("/capturar-orden/{orderId}")
    public PaypalCapturaResponse capturarOrden(@PathVariable String orderId) {
        return paypalService.capturarOrden(orderId);
    }

    @PostMapping("/confirmar-pedido/{orderId}")
    public ResponseEntity<ApiResponse<PedidoResponse>> confirmarPedido(
            @PathVariable String orderId,
            @RequestBody PedidoRequest request,
            Authentication authentication)
            throws UsuarioNotFoundException, ProductoNotFoundException, StockInsuficienteException {
        PedidoResponse response = paypalService.confirmarPedidoPaypal(
            orderId,
            request,
            authentication == null ? null : authentication.getName()
        );

        return ResponseEntity.ok(new ApiResponse<>("Pedido creado con exito", response));
    }
}
