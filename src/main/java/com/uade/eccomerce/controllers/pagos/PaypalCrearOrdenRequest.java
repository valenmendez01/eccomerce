package com.uade.eccomerce.controllers.pagos;

import java.util.List;

import com.uade.eccomerce.controllers.pedidos.ItemRequest;

import lombok.Data;

@Data
public class PaypalCrearOrdenRequest {
    private List<ItemRequest> items;
    private String returnUrl;
    private String cancelUrl;
}
