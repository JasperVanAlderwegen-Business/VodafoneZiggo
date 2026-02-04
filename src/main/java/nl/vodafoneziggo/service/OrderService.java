package nl.vodafoneziggo.service;

import lombok.RequiredArgsConstructor;
import nl.vodafoneziggo.external.reqres.ReqresClient;
import nl.vodafoneziggo.external.reqres.ReqresUser;
import nl.vodafoneziggo.model.OrderEntity;
import nl.vodafoneziggo.orders.model.CreateOrderRequest;
import nl.vodafoneziggo.orders.model.CreateOrderResponse;
import nl.vodafoneziggo.repository.OrderRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ReqresClient reqresClient;


    public CreateOrderResponse createOrder(CreateOrderRequest createOrderRequest) {
        ReqresUser user = reqresClient.findUserByEmail(createOrderRequest.getEmail())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Email does not exist in external user system"
                ));

        if (orderRepository.findByEmailAndProductID(createOrderRequest.getEmail(), createOrderRequest.getProductID()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Order already exists for this user and product");
        }
        // TODO: Validate email
        // TODO: Check if order already exists for user
        // TODO: Persist order
        // TODO: Return order ID
        if (orderRepository.findByEmailAndProductID(createOrderRequest.getEmail(), createOrderRequest.getProductID()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Order already exists for this user and product");
        }
        OrderEntity order = new OrderEntity();
        order.setEmail(createOrderRequest.getEmail());
        order.setProductID(createOrderRequest.getProductID());
        order.setFirstName(user.getFirstName());
        order.setLastName(user.getLastName());
        return new CreateOrderResponse(orderRepository.save(order).getId());
    }
}
