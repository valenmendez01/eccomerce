package com.uade.eccomerce.exceptions.productos;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "El producto no fue encontrado")
public class ProductoNotFoundException extends Exception {}