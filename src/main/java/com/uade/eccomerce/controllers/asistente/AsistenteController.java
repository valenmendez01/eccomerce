package com.uade.eccomerce.controllers.asistente;

import com.uade.eccomerce.controllers.ApiResponse;
import com.uade.eccomerce.controllers.asistente.dto.AsistenteRequest;
import com.uade.eccomerce.controllers.asistente.dto.AsistenteResponse;
import com.uade.eccomerce.service.asistente.AsistenteService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/asistente")
@RequiredArgsConstructor
public class AsistenteController {

    private final AsistenteService asistenteService;

    @PostMapping("/preguntar")
    public ResponseEntity<ApiResponse<AsistenteResponse>> preguntar(@RequestBody AsistenteRequest request) {
        return ResponseEntity.ok(
            new ApiResponse<>("Respuesta del asistente obtenida", asistenteService.responder(request))
        );
    }
}
