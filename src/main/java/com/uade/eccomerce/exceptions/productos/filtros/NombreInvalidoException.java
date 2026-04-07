package com.uade.eccomerce.exceptions.productos.filtros;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "El nombre de búsqueda no puede estar vacío")
public class NombreInvalidoException extends Exception {}