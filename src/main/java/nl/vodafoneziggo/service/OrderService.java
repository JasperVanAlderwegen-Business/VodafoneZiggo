package nl.vodafoneziggo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.vodafoneziggo.external.reqres.ReqresClient;
import nl.vodafoneziggo.external.reqres.ReqresUser;
import nl.vodafoneziggo.model.OrderEntity;
import nl.vodafoneziggo.orders.model.CreateOrderRequest;
import nl.vodafoneziggo.orders.model.CreateOrderResponse;
import nl.vodafoneziggo.orders.model.Order;
import nl.vodafoneziggo.orders.model.UpdateOrderRequest;
import nl.vodafoneziggo.repository.OrderRepository;
import org.apache.camel.Header;
import org.apache.logging.log4j.util.Strings;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * This service handles operations related to orders, including creating a new order
 * and retrieving orders for a specific user.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final ReqresClient reqresClient;

    /**
     * Creates a new order for a user based on the provided request details. This method validates
     * the user's email through an external system, checks for existing orders for the same product
     * and user, and persists the new order if validations pass.
     *
     * @param createOrderRequest the request object containing details of the order to create, including
     *                           the email of the user and the product ID.
     * @return a {@link CreateOrderResponse} containing the ID of the newly created order.
     * @throws ResponseStatusException if the email does not exist in the external user system
     *                                 or if a duplicate order already exists for the given user and product.
     */
    public CreateOrderResponse createOrder(CreateOrderRequest createOrderRequest) {
        ReqresUser user = getReqresUser(createOrderRequest.getEmail());

        if (orderRepository.findByEmailAndProductID(createOrderRequest.getEmail(), createOrderRequest.getProductID())
                .isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "The customer already ordered the same product");
        }
        OrderEntity order = new OrderEntity();
        order.setEmail(createOrderRequest.getEmail());
        order.setProductID(createOrderRequest.getProductID());
        order.setFirstName(user.getFirstName());
        order.setLastName(user.getLastName());
        return new CreateOrderResponse(orderRepository.save(order).getId());
    }

    /**
     * Retrieves a list of orders based on the provided email. If the email is valid and not blank,
     * it ensures the email exists in an external user system before fetching orders associated with
     * that email. If no email is provided, all orders are retrieved.
     *
     * @param email the email address of the user whose orders are to be retrieved; if blank or null, all orders are retrieved.
     * @return a list of {@link OrderEntity} objects representing the orders associated with the given email,
     * or all orders if no email is provided.
     */
    public List<Order> listOrders(String email) {
        List<OrderEntity> orders;
        if (!Strings.isBlank(email)) {
            getReqresUser(email);
            orders = (List<OrderEntity>) orderRepository.findByEmail(email);
        } else {
            orders = (List<OrderEntity>) orderRepository.findAll();
        }
        return orders.stream().map(o -> new Order(o.getId().toString(), o.getEmail(), o.getFirstName(),
                o.getLastName(), o.getProductID().toString())).toList();
    }

    /**
     * Deletes an existing order identified by the provided order ID. If the order
     * does not exist in the repository, an HTTP 404 (Not Found) exception is thrown.
     *
     * @param orderID the unique identifier of the order to be deleted
     * @throws ResponseStatusException if the order with the given ID is not found
     */
    public void deleteOrder(Integer orderID) {
        OrderEntity order = orderRepository.findById(orderID).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        orderRepository.delete(order);
        log.info("Order with ID {} deleted successfully", orderID);
    }

    /**
     * Updates an existing order by changing its owner (email) and associated user details.
     * The new email must exist in the external user system.
     *
     * @param orderID            the unique identifier of the order to update
     * @param updateOrderRequest object containing the new customer email address
     * @return the updated {@link Order} object
     * @throws ResponseStatusException if the order is not found, the new email doesn't exist in reqres.in,
     *                                 or if transferring to the new email would create a duplicate
     */
    public Order updateOrder(@Header("orderID") Integer orderID, UpdateOrderRequest updateOrderRequest) {
        if (updateOrderRequest == null || updateOrderRequest.getEmail() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "email must not be null");
        }
        String newEmail = updateOrderRequest.getEmail();
        // Find the order
        OrderEntity order = orderRepository.findById(orderID)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Order not found"
                ));
        // Validate new email exists in reqres
        ReqresUser newUser = getReqresUser(newEmail);
        // Check if the new user already has this product
        if (!newEmail.equals(order.getEmail()) &&
                orderRepository.findByEmailAndProductID(newEmail, order.getProductID()).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Order already exists for this user and product"
            );
        }
        // Update the order
        order.setEmail(newEmail);
        order.setFirstName(newUser.getFirstName());
        order.setLastName(newUser.getLastName());
        OrderEntity updated = orderRepository.save(order);
        log.info("Order {} transferred to new owner: {}", orderID, newEmail);
        // Return as Order DTO
        return new Order(
                updated.getId().toString(),
                updated.getEmail(),
                updated.getFirstName(),
                updated.getLastName(),
                updated.getProductID().toString()
        );
    }

    private @NonNull ReqresUser getReqresUser(String email) {
        return reqresClient.findUserByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Email " + email + " does not exist in external user system"
                ));
    }
}
