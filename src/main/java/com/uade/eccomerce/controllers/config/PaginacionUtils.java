package com.uade.eccomerce.controllers.config;

import org.springframework.data.domain.PageRequest;

import com.uade.eccomerce.exceptions.SolicitudInvalidaException;

public final class PaginacionUtils {

    private static final int TAMANIO_POR_DEFECTO = 20;
    private static final int TAMANIO_MAXIMO = 100;

    private PaginacionUtils() {
    }

    public static PageRequest crear(Integer page, Integer size) {
        int pagina = page == null ? 0 : page;
        int tamanioSolicitado = size == null ? TAMANIO_POR_DEFECTO : size;

        if (pagina < 0 || tamanioSolicitado <= 0) {
            throw new SolicitudInvalidaException("La paginacion es invalida.");
        }

        return PageRequest.of(pagina, Math.min(tamanioSolicitado, TAMANIO_MAXIMO));
    }
}
