package nl.vodafoneziggo.routes;

import lombok.RequiredArgsConstructor;
import nl.vodafoneziggo.service.OrderService;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderRoutes extends RouteBuilder {
    private final OrderService orderService;

    @Override
    public void configure() {
        from("direct:createOrder").bean(orderService, "createOrder");
    }
}
