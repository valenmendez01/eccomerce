package com.uade.eccomerce.exceptions.productos;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "No hay stock suficiente para uno de los productos")
public class StockInsuficienteException extends Exception{}
