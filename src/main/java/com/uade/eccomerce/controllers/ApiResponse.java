package com.uade.eccomerce.controllers;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

/*
  * Clase genérica para estandarizar las respuestas de la API, encapsulando un mensaje y los datos de la respuesta.
  * Permite una estructura uniforme en todas las respuestas, facilitando el manejo de errores y la interpretación de los resultados por parte del cliente.
  * @param <T> El tipo de datos que se incluirá en la respuesta (por ejemplo, un objeto, una lista, etc.)
*/

public class ApiResponse<T> {
    private String mensaje;
    private T data;
}
