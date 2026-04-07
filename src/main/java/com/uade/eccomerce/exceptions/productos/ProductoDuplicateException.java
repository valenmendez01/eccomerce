package com.uade.eccomerce.exceptions.productos;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Ya existe un producto con ese nombre")
public class ProductoDuplicateException extends Exception {}