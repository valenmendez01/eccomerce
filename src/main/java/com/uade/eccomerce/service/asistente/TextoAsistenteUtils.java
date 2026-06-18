package com.uade.eccomerce.service.asistente;

import java.text.Normalizer;
import java.util.Locale;

public final class TextoAsistenteUtils {

    private TextoAsistenteUtils() {
    }

    public static String normalizarTexto(String texto) {
        if (texto == null) return "";
        return Normalizer.normalize(texto, Normalizer.Form.NFD)
            .replaceAll("\\p{M}", "")
            .toLowerCase(Locale.ROOT)
            .trim();
    }

    public static String capitalizar(String texto) {
        if (texto == null || texto.isBlank()) return "";
        String limpio = texto.trim();
        return limpio.substring(0, 1).toUpperCase(Locale.ROOT) + limpio.substring(1);
    }
}
