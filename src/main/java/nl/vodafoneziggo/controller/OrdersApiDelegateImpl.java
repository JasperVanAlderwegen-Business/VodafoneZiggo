package nl.vodafoneziggo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.vodafoneziggo.orders.api.OrdersApiDelegate;
import nl.vodafoneziggo.orders.model.CreateOrderRequest;
import nl.vodafoneziggo.orders.model.CreateOrderResponse;
import nl.vodafoneziggo.orders.model.Order;
import nl.vodafoneziggo.orders.model.UpdateOrderRequest;
import org.apache.camel.ProducerTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Implementation of the {@code OrdersApiDelegate} interface responsible for handling order-related operations.
 * This class acts as a delegate for the {@code OrdersApiController} to process API requests for creating and
 * retrieving orders.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OrdersApiDelegateImpl implements OrdersApiDelegate {
    private final ProducerTemplate producerTemplate;

    /**
     * Creates a new order based on the provided request.
     *
     * @param createOrderRequest the request object containing the order details, including product ID and customer email
     * @return a {@code ResponseEntity} containing the created order details wrapped in a {@code CreateOrderResponse} object with HTTP status 201
     */
    @Override
    public ResponseEntity<CreateOrderResponse> createOrder(CreateOrderRequest createOrderRequest) {
        CreateOrderResponse response = producerTemplate.requestBody("direct:createOrder", createOrderRequest,
                CreateOrderResponse.class);
        return ResponseEntity.status(201).body(response);
    }

    /**
     * Retrieves a list of orders associated with the given email.
     *
     * @param email the email address used to filter the orders
     * @return a {@code ResponseEntity} containing a list of {@code Order} objects
     */
    @SuppressWarnings("unchecked") // The return type of producerTemplate.requestBody is List<Order>
    @Override
    public ResponseEntity<List<Order>> listOrders(String email) {
        List<Order> orders = producerTemplate.requestBody("direct:listOrders", email, List.class);
        log.info("Retrieved {} orders for email {}", orders.size(), email);
        return ResponseEntity.ok(orders);
    }

    /**
     * Deletes an order identified by the provided order ID.
     *
     * @param orderID the unique identifier of the order to be deleted
     * @return a {@code ResponseEntity<Void>} with HTTP status 204 if the deletion is successful
     */
    @Override
    public ResponseEntity<Void> deleteOrder(Integer orderID) {
        producerTemplate.sendBody("direct:deleteOrder", orderID);
        return ResponseEntity.noContent().build();
    }

    /**
     * Updates an existing order identified by the provided order ID with the new details specified in the update request.
     *
     * @param orderID            the unique identifier of the order to be updated
     * @param updateOrderRequest the request object containing the updated order details, such as email
     * @return a {@code ResponseEntity} containing the updated {@code Order} object
     */
    @Override
    public ResponseEntity<Order> updateOrder(Integer orderID, UpdateOrderRequest updateOrderRequest) {
        String email = (updateOrderRequest != null) ? updateOrderRequest.getEmail() : null;
        log.debug("Updating order {} with new email {}", orderID, email);

        Order updatedOrder = producerTemplate.requestBodyAndHeader(
                "direct:updateOrder",
                updateOrderRequest,
                "orderID",
                orderID,
                Order.class
        );
        return ResponseEntity.ok(updatedOrder);
    }
}
