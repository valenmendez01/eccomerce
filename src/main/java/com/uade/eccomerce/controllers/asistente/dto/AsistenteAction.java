package com.uade.eccomerce.controllers.asistente.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AsistenteAction {
    private String texto;
    private String tipo;
    private Map<String, Object> filtro;
    private String ruta;
    private String flujo;
}
