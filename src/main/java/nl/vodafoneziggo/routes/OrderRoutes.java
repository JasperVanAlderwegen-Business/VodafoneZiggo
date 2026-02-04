package nl.vodafoneziggo.routes;

import lombok.RequiredArgsConstructor;
import nl.vodafoneziggo.service.OrderService;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Configures the routing for order-related operations.
 * This class defines Camel routes to handle order creation and retrieval
 * functionalities by delegating the logic to the {@link OrderService}.
 * <p>
 * Routes:
 * - "direct:createOrder": Invokes the {@code createOrder} method of {@link OrderService}
 * to create a new order.
 * - "direct:listOrders": Invokes the {@code listOrders} method of {@link OrderService}
 * to retrieve a list of orders.
 * <p>
 * This component serves as a bridge between the integration routes
 * and the order processing logic encapsulated in the service layer.
 * <p>
 * This class extends {@link RouteBuilder} to define Apache Camel routes programmatically.
 */
@Component
@RequiredArgsConstructor
public class OrderRoutes extends RouteBuilder {
    private final OrderService orderService;

    @Override
    public void configure() {
        from("direct:createOrder").bean(orderService, "createOrder");
        from("direct:listOrders").bean(orderService, "listOrders");
    }
}
