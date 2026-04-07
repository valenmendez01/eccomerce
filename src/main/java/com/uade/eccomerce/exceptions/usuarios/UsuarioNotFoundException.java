package com.uade.eccomerce.exceptions.usuarios;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "El usuario/vendedor no existe")
public class UsuarioNotFoundException extends Exception {}