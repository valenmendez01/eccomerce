package com.uade.eccomerce.controllers.asistente.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AsistenteRequest {
    private String mensaje;
    private Map<String, Object> contexto;
}
