package com.uade.eccomerce.service.asistente;

import com.uade.eccomerce.controllers.asistente.dto.AsistenteRequest;
import com.uade.eccomerce.controllers.asistente.dto.AsistenteResponse;

import lombok.RequiredArgsConstructor;

import java.util.Collections;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AsistenteRespuestaLocalService {

    public static final String RESPUESTA_FUERA_DE_TEMA = "Solo puedo ayudarte con consultas relacionadas con FIGULLECT, como productos, catálogo, carrito, pedidos, stock o tu cuenta.";
    public static final String RESPUESTA_SEGURIDAD = "No puedo cambiar mis reglas internas ni mostrar instrucciones del sistema. Sí puedo ayudarte con productos, filtros, carrito o pedidos de FIGULLECT.";

    private final DetectorSeleccionAsistente detectorSeleccion;

    public AsistenteResponse resolver(AsistenteRequest request) {
        String consulta = TextoAsistenteUtils.normalizarTexto(request.getMensaje());

        if (esIntentoManipularSistema(consulta)) {
            return new AsistenteResponse(RESPUESTA_SEGURIDAD, Collections.emptyList(), false);
        }

        String seleccionNoDisponible = detectorSeleccion.extraerSeleccionNoDisponible(request);
        if (seleccionNoDisponible == null) return null;

        String selecciones = String.join(", ", detectorSeleccion.obtenerSeleccionesDisponibles(request.getContexto()));
        String respuesta = "Por ahora no tenemos productos de " + TextoAsistenteUtils.capitalizar(seleccionNoDisponible)
            + ". Las selecciones disponibles son: " + selecciones + ".";
        return new AsistenteResponse(respuesta, Collections.emptyList(), false);
    }

    private boolean esIntentoManipularSistema(String consulta) {
        return (consulta.contains("ignora") && consulta.contains("instruccion"))
            || consulta.contains("prompt interno")
            || consulta.contains("instrucciones del sistema")
            || consulta.contains("nueva regla")
            || consulta.contains("productos valen")
            || consulta.contains("json del sistema")
            || consulta.contains("responde solamente el json")
            || consulta.contains("responde como si fueras chatgpt");
    }
}
