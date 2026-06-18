package com.uade.eccomerce.service.asistente;

import com.uade.eccomerce.controllers.asistente.dto.AsistenteRequest;
import com.uade.eccomerce.controllers.asistente.dto.AsistenteResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AsistenteService {

    private final OpenAiService openAiService;

    public AsistenteResponse responder(AsistenteRequest request) {
        if (request == null || request.getMensaje() == null || request.getMensaje().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La consulta del asistente es obligatoria.");
        }

        return openAiService.generarRespuesta(request);
    }
}
