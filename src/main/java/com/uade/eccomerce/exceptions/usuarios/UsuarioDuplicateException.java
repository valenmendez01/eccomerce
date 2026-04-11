package com.uade.eccomerce.exceptions.usuarios;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Ya existe un usuario registrado con ese email")
public class UsuarioDuplicateException extends Exception {}