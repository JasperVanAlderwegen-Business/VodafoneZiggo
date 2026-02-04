package nl.vodafoneziggo.controller;

import lombok.RequiredArgsConstructor;
import nl.vodafoneziggo.orders.api.OrdersApi;
import nl.vodafoneziggo.orders.model.CreateOrderRequest;
import nl.vodafoneziggo.orders.model.CreateOrderResponse;
import nl.vodafoneziggo.orders.model.Order;
import org.apache.camel.ProducerTemplate;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RequiredArgsConstructor
public class OrdersController implements OrdersApi {
    private final ProducerTemplate producerTemplate;

    @Override
    public ResponseEntity<CreateOrderResponse> createOrder(CreateOrderRequest createOrderRequest) {
        CreateOrderResponse response = producerTemplate.requestBody("direct:createOrder", createOrderRequest, CreateOrderResponse.class);
        return ResponseEntity.status(201).body(response);
    }

    @Override
    public ResponseEntity<List<Order>> listOrders(String email) {
        return OrdersApi.super.listOrders(email);
    }
}
