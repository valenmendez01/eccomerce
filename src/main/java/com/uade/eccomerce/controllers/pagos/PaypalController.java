package com.uade.eccomerce.controllers.pagos;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
