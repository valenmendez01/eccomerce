package com.uade.eccomerce.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN, reason = "No tenes permiso para ver este pedido")
public class AccesoPedidoNoAutorizadoException extends RuntimeException {
}
