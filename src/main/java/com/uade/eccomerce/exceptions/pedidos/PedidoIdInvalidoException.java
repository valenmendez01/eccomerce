package com.uade.eccomerce.exceptions.pedidos;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "El ID del pedido es inválido o nulo")
public class PedidoIdInvalidoException extends Exception {}
