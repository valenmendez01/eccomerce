package com.uade.eccomerce.exceptions.pedidos;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "El pedido solicitado no existe")
public class PedidoNotFoundException extends Exception {}
