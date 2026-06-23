package com.uade.eccomerce.controllers.ventas;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uade.eccomerce.controllers.ApiResponse;
import com.uade.eccomerce.controllers.pedidos.PedidoResponse;
import com.uade.eccomerce.exceptions.usuarios.UsuarioNotFoundException;
import com.uade.eccomerce.service.pedido.PedidoService;

@RestController
@RequestMapping("/ventas")
public class VentasController {

    private static final int TAMANIO_PAGINA_POR_DEFECTO = 100;

    @Autowired
    private PedidoService pedidoService;

    @GetMapping("/vendedor")
    public ResponseEntity<ApiResponse<Page<PedidoResponse>>> obtenerVentasDelVendedor(
            Authentication authentication,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size)
            throws UsuarioNotFoundException {
        PageRequest pageable = PageRequest.of(
                page == null ? 0 : page,
                size == null ? TAMANIO_PAGINA_POR_DEFECTO : size);

        return ResponseEntity.ok(new ApiResponse<>("Ventas del vendedor obtenidas",
                pedidoService.obtenerVentasDelVendedor(authentication == null ? null : authentication.getName(), pageable)));
    }
}
