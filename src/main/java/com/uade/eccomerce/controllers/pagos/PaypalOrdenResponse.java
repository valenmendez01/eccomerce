package com.uade.eccomerce.controllers.pagos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaypalOrdenResponse {
    private String orderId;
    private String approvalUrl;
    private String estado;
    private String moneda;
    private String total;
}
