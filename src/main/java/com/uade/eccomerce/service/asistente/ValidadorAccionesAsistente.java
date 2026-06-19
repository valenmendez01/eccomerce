package com.uade.eccomerce.service.asistente;

import com.uade.eccomerce.controllers.asistente.dto.AsistenteAction;
import com.uade.eccomerce.controllers.asistente.dto.AsistenteRequest;
import com.uade.eccomerce.controllers.asistente.dto.AsistenteResponse;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ValidadorAccionesAsistente {

    private final DetectorSeleccionAsistente detectorSeleccion;

    public AsistenteResponse validar(AsistenteResponse respuesta, AsistenteRequest request) {
        if (respuesta.getRespuesta() == null || respuesta.getRespuesta().isBlank()) {
            respuesta.setRespuesta(AsistenteRespuestaLocalService.RESPUESTA_FUERA_DE_TEMA);
            respuesta.setFueraDeTema(true);
        }

        if (respuesta.getAcciones() == null) respuesta.setAcciones(Collections.emptyList());
        if (respuesta.getFueraDeTema() == null) respuesta.setFueraDeTema(false);

        respuesta.setAcciones(validarAcciones(respuesta.getAcciones(), request));
        return respuesta;
    }

    private List<AsistenteAction> validarAcciones(List<AsistenteAction> acciones, AsistenteRequest request) {
        List<String> seleccionesDisponibles = detectorSeleccion.obtenerSeleccionesDisponibles(request.getContexto());

        return acciones.stream()
            .map(accion -> validarAccion(accion, seleccionesDisponibles))
            .filter(accion -> accion != null && accion.getTexto() != null && !accion.getTexto().isBlank())
            .toList();
    }

    private AsistenteAction validarAccion(AsistenteAction accion, List<String> seleccionesDisponibles) {
        if (accion == null || accion.getTipo() == null || "ninguno".equals(accion.getTipo())) return null;
        if (!"aplicarFiltro".equals(accion.getTipo())) return accion;
        if (accion.getFiltro() == null) return null;

        Map<String, Object> filtro = new LinkedHashMap<>(accion.getFiltro());
        if (!seleccionesFiltroValidas(filtro, seleccionesDisponibles)) return null;
        if (tieneSeleccion(filtro) && tieneSoloCategoriaFiguritas(filtro)) {
            filtro.put("categoria", null);
            filtro.put("categorias", null);
        }

        accion.setFiltro(filtro);
        return accion;
    }

    private boolean seleccionesFiltroValidas(Map<String, Object> filtro, List<String> seleccionesDisponibles) {
        List<String> selecciones = obtenerValoresFiltro(filtro.get("seleccion"));
        selecciones.addAll(obtenerValoresFiltro(filtro.get("selecciones")));

        return selecciones.stream().allMatch(seleccion ->
            detectorSeleccion.esSeleccionDisponible(seleccion, seleccionesDisponibles)
        );
    }

    private boolean tieneSeleccion(Map<String, Object> filtro) {
        return !obtenerValoresFiltro(filtro.get("seleccion")).isEmpty()
            || !obtenerValoresFiltro(filtro.get("selecciones")).isEmpty();
    }

    private boolean tieneSoloCategoriaFiguritas(Map<String, Object> filtro) {
        List<String> categorias = obtenerValoresFiltro(filtro.get("categoria"));
        categorias.addAll(obtenerValoresFiltro(filtro.get("categorias")));
        return categorias.size() == 1 && "figuritas".equals(TextoAsistenteUtils.normalizarTexto(categorias.get(0)));
    }

    private List<String> obtenerValoresFiltro(Object valor) {
        List<String> valores = new ArrayList<>();

        if (valor instanceof String texto && !texto.isBlank()) valores.add(texto);
        if (valor instanceof List<?> lista) {
            lista.stream()
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .filter(texto -> !texto.isBlank())
                .forEach(valores::add);
        }

        return valores;
    }
}
