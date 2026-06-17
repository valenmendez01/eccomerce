package com.uade.eccomerce.controllers.pagos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaypalCapturaResponse {
    private String orderId;
    private String estado;
}
