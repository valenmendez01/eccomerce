package com.uade.eccomerce.exceptions.productos.filtros;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "La categoría proporcionada es nula o inválida")
public class CategoriaInvalidaException extends Exception {}