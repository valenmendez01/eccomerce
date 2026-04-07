package com.uade.eccomerce.exceptions.productos.filtros;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "El rango de precios es ilógico (ej: min > max) o alguno de los precios es nulo")
public class PrecioInvalidoException extends Exception {}