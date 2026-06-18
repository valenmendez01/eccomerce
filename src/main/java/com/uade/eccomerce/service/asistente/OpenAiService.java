package com.uade.eccomerce.service.asistente;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.uade.eccomerce.controllers.asistente.dto.AsistenteRequest;
import com.uade.eccomerce.controllers.asistente.dto.AsistenteResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class OpenAiService {

    private final AsistenteRespuestaLocalService respuestaLocalService;
    private final OpenAiSolicitudBuilder solicitudBuilder;
    private final OpenAiRespuestaExtractor respuestaExtractor;
    private final ValidadorAccionesAsistente validadorAcciones;
    private final RestClient restClient = RestClient.builder()
        .requestFactory(crearRequestFactory())
        .build();

    @Value("${openai.api.key:}")
    private String apiKey;

    @Value("${openai.api.url:https://api.openai.com}")
    private String apiUrl;

    public AsistenteResponse generarRespuesta(AsistenteRequest request) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "OpenAI no esta configurado.");
        }

        AsistenteResponse respuestaLocal = respuestaLocalService.resolver(request);
        if (respuestaLocal != null) return respuestaLocal;

        try {
            String respuestaApi = restClient.post()
                .uri(crearUrlResponses())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(solicitudBuilder.crearPayload(request))
                .retrieve()
                .body(String.class);

            AsistenteResponse respuesta = respuestaExtractor.extraer(respuestaApi);
            return validadorAcciones.validar(respuesta, request);
        } catch (RestClientResponseException error) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "No se pudo consultar OpenAI.", error);
        } catch (JsonProcessingException error) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "No se pudo interpretar la respuesta de OpenAI.", error);
        }
    }

    private String crearUrlResponses() {
        return apiUrl.replaceAll("/+$", "") + "/v1/responses";
    }

    private static SimpleClientHttpRequestFactory crearRequestFactory() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(10000);
        requestFactory.setReadTimeout(30000);
        return requestFactory;
    }
}
