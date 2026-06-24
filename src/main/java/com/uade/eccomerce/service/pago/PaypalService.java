package com.uade.eccomerce.service.pago;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.uade.eccomerce.controllers.pedidos.ItemRequest;
import com.uade.eccomerce.controllers.pagos.PaypalCapturaResponse;
import com.uade.eccomerce.controllers.pagos.PaypalCrearOrdenRequest;
import com.uade.eccomerce.controllers.pagos.PaypalOrdenResponse;
import com.uade.eccomerce.entity.Producto;
import com.uade.eccomerce.exceptions.SolicitudInvalidaException;
import com.uade.eccomerce.repository.ProductoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaypalService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ProductoRepository productoRepository;

    @Value("${paypal.client-id}")
    private String clientId;

    @Value("${paypal.client-secret}")
    private String clientSecret;

    @Value("${paypal.base-url}")
    private String baseUrl;

    @Value("${paypal.pesos-por-dolar}")
    private BigDecimal pesosPorDolar;

    private String accessTokenCache;
    private long accessTokenExpiraEn;

    public PaypalOrdenResponse crearOrden(PaypalCrearOrdenRequest request) {
        String accessToken = obtenerAccessToken();
        String total = convertirPesosADolares(calcularTotalPesos(request.getItems()));

        Map<String, Object> body = Map.of(
            "intent", "CAPTURE",
            "purchase_units", List.of(Map.of(
                "amount", Map.of("currency_code", "USD", "value", total)
            )),
            "application_context", Map.of(
                "return_url", request.getReturnUrl(),
                "cancel_url", request.getCancelUrl(),
                "user_action", "PAY_NOW"
            )
        );

        Map<?, ?> response = restTemplate.postForObject(
            baseUrl + "/v2/checkout/orders",
            new HttpEntity<>(body, crearHeadersBearer(accessToken)),
            Map.class
        );

        return new PaypalOrdenResponse(
            String.valueOf(response.get("id")),
            obtenerApprovalUrl(response),
            String.valueOf(response.get("status")),
            "USD",
            total
        );
    }

    public PaypalCapturaResponse capturarOrden(String orderId) {
        String accessToken = obtenerAccessToken();
        Map<?, ?> response = restTemplate.postForObject(
            baseUrl + "/v2/checkout/orders/" + orderId + "/capture",
            new HttpEntity<>(Map.of(), crearHeadersBearer(accessToken)),
            Map.class
        );

        return new PaypalCapturaResponse(
            String.valueOf(response.get("id")),
            String.valueOf(response.get("status"))
        );
    }

    private synchronized String obtenerAccessToken() {
        if (accessTokenCache != null && System.currentTimeMillis() < accessTokenExpiraEn) {
            return accessTokenCache;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Basic " + obtenerCredencialesBase64());

        LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");

        Map<?, ?> response = restTemplate.postForObject(
            baseUrl + "/v1/oauth2/token",
            new HttpEntity<>(body, headers),
            Map.class
        );

        Number expiresIn = (Number) response.get("expires_in");
        accessTokenCache = String.valueOf(response.get("access_token"));
        accessTokenExpiraEn = System.currentTimeMillis() + ((expiresIn.longValue() - 60) * 1000);

        return accessTokenCache;
    }

    private HttpHeaders crearHeadersBearer(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);
        return headers;
    }

    private String obtenerCredencialesBase64() {
        String credenciales = clientId + ":" + clientSecret;
        return Base64.getEncoder().encodeToString(
            credenciales.getBytes(StandardCharsets.UTF_8)
        );
    }

    private double calcularTotalPesos(List<ItemRequest> items) {
        if (items == null || items.isEmpty()) {
            throw new SolicitudInvalidaException("El pago debe tener al menos un producto.");
        }

        return items.stream()
            .mapToDouble((item) -> calcularSubtotalItem(item))
            .sum();
    }

    private double calcularSubtotalItem(ItemRequest item) {
        if (item == null || item.getIdProducto() == null || item.getCantidad() == null || item.getCantidad() <= 0) {
            throw new SolicitudInvalidaException("Los items del pago son invalidos.");
        }

        Producto producto = productoRepository.findById(item.getIdProducto())
            .orElseThrow(() -> new SolicitudInvalidaException("Uno de los productos del pago no existe."));

        double precio = producto.getPrecio() == null ? 0 : producto.getPrecio();
        int descuento = producto.getDescuento() == null ? 0 : producto.getDescuento();
        double precioFinal = Math.round(precio * (1 - descuento / 100.0));

        return precioFinal * item.getCantidad();
    }

    private String convertirPesosADolares(double totalPesos) {
        if (totalPesos <= 0) {
            throw new IllegalArgumentException("El total del pago debe ser mayor a cero.");
        }

        return BigDecimal.valueOf(totalPesos)
            .divide(pesosPorDolar, 2, RoundingMode.HALF_UP)
            .max(BigDecimal.valueOf(1))
            .toPlainString();
    }

    private String obtenerApprovalUrl(Map<?, ?> response) {
        List<?> links = (List<?>) response.get("links");

        return links.stream()
            .filter(Map.class::isInstance)
            .map(Map.class::cast)
            .filter((link) -> "approve".equals(link.get("rel")))
            .map((link) -> String.valueOf(link.get("href")))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("PayPal no devolvio URL de aprobacion."));
    }
}
