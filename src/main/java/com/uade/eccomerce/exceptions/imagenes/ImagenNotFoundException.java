package com.uade.eccomerce.exceptions.imagenes;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "La imagen no fue encontrada")
public class ImagenNotFoundException extends Exception {}