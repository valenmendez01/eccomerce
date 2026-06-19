package com.uade.eccomerce.service.asistente;

import com.uade.eccomerce.controllers.asistente.dto.AsistenteRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

@Component
public class DetectorSeleccionAsistente {

    private static final Pattern PATRON_CANDIDATO_SELECCION =
        Pattern.compile("\\b(?:de|del|para)\\s+([\\p{L}\\s]{2,40})", Pattern.CASE_INSENSITIVE);

    private static final List<String> PALABRAS_CORTE_SELECCION = List.of(
        "y", "pero", "con", "sin", "que", "no", "barato", "barata", "caro", "cara",
        "economico", "economica", "premium", "disponible", "disponibles"
    );

    public String extraerSeleccionNoDisponible(AsistenteRequest request) {
        String consulta = TextoAsistenteUtils.normalizarTexto(request.getMensaje());
        if (!consulta.contains("figurita") && !consulta.contains("producto") && !consulta.contains("seleccion")) {
            return null;
        }

        String candidato = extraerCandidatoSeleccion(request.getMensaje());
        if (candidato == null || candidato.isBlank()) return null;
        if (esSeleccionDisponible(candidato, obtenerSeleccionesDisponibles(request.getContexto()))) return null;
        if (apareceComoProductoVisible(candidato, request.getContexto())) return null;
        return candidato;
    }

    public List<String> obtenerSeleccionesDisponibles(Map<String, Object> contexto) {
        Object valor = contexto == null ? null : contexto.get("seleccionesDisponibles");
        if (!(valor instanceof List<?> lista)) return Collections.emptyList();

        return lista.stream()
            .filter(String.class::isInstance)
            .map(String.class::cast)
            .toList();
    }

    public boolean esSeleccionDisponible(String candidato, List<String> seleccionesDisponibles) {
        String candidatoNormalizado = TextoAsistenteUtils.normalizarTexto(candidato);
        return seleccionesDisponibles.stream()
            .map(TextoAsistenteUtils::normalizarTexto)
            .anyMatch(seleccion -> seleccion.equals(candidatoNormalizado));
    }

    private String extraerCandidatoSeleccion(String mensaje) {
        Matcher matcher = PATRON_CANDIDATO_SELECCION.matcher(mensaje == null ? "" : mensaje);

        while (matcher.find()) {
            String candidato = limpiarCandidatoSeleccion(matcher.group(1));
            if (!candidato.isBlank()) return candidato;
        }

        return null;
    }

    private String limpiarCandidatoSeleccion(String candidatoCrudo) {
        String[] palabras = candidatoCrudo.replaceAll("[^\\p{L}\\s]", " ").trim().split("\\s+");
        List<String> utiles = new ArrayList<>();

        for (String palabra : palabras) {
            String normalizada = TextoAsistenteUtils.normalizarTexto(palabra);
            if (normalizada.isBlank() || PALABRAS_CORTE_SELECCION.contains(normalizada)) break;
            utiles.add(palabra);
            if (utiles.size() == 2) break;
        }

        return String.join(" ", utiles);
    }

    private boolean apareceComoProductoVisible(String candidato, Map<String, Object> contexto) {
        Object valor = contexto == null ? null : contexto.get("productosVisibles");
        if (!(valor instanceof List<?> productos)) return false;

        String candidatoNormalizado = TextoAsistenteUtils.normalizarTexto(candidato);
        return productos.stream().anyMatch(producto -> {
            if (!(producto instanceof Map<?, ?> productoMap)) return false;
            Object nombre = productoMap.get("nombre");
            return nombre != null && TextoAsistenteUtils.normalizarTexto(String.valueOf(nombre)).contains(candidatoNormalizado);
        });
    }
}
