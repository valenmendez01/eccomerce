package com.uade.eccomerce.exceptions.productos;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "El ID proporcionado no puede ser nulo")
public class ProductoIdInvalidoException extends Exception {}