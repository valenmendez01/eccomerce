package com.uade.eccomerce.controllers.imagenes;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImagenResponse {
    private Long idImagen;
    private String url;
    private String contenidoBase64;
}
