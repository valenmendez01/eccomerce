package com.uade.eccomerce.service.asistente;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uade.eccomerce.controllers.asistente.dto.AsistenteResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OpenAiRespuestaExtractor {

    private final ObjectMapper objectMapper;

    public AsistenteResponse extraer(String respuestaApi) throws JsonProcessingException {
        JsonNode raiz = objectMapper.readTree(respuestaApi);
        String texto = raiz.path("output_text").asText(null);

        if (texto == null || texto.isBlank()) {
            texto = extraerTextoDesdeOutput(raiz.path("output"));
        }

        if (texto == null || texto.isBlank()) {
            throw new JsonProcessingException("La respuesta de OpenAI no incluyo texto.") {};
        }

        return objectMapper.readValue(texto, AsistenteResponse.class);
    }

    private String extraerTextoDesdeOutput(JsonNode output) {
        if (!output.isArray()) return null;

        for (JsonNode item : output) {
            JsonNode content = item.path("content");
            if (!content.isArray()) continue;

            for (JsonNode contenido : content) {
                if ("output_text".equals(contenido.path("type").asText())) {
                    return contenido.path("text").asText(null);
                }
            }
        }

        return null;
    }
}
