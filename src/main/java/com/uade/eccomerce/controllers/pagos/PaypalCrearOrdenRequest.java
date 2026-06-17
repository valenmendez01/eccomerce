package com.uade.eccomerce.controllers.pagos;

import lombok.Data;

@Data
public class PaypalCrearOrdenRequest {
    private Double totalPesos;
    private String returnUrl;
    private String cancelUrl;
}
