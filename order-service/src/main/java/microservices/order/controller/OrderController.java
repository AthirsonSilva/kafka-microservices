package microservices.order.controller;

import lombok.RequiredArgsConstructor;
import microservices.domains.events.OrderEvent;
import microservices.domains.payload.Order;
import microservices.order.enums.OrderEventStatus;
import microservices.order.kafka.OrderProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderProducer orderProducer;
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderController.class);

    @PostMapping
    public ResponseEntity<String> placeOrder(@RequestBody Order order) {
        LOGGER.info(String.format("Received order: %s", order.toString()));

        order.setId(UUID.randomUUID().toString());

        OrderEvent event = new OrderEvent();
        event.setStatus(OrderEventStatus.PENDING.name());
        event.setMessage(String.format("Order is in %s status...", OrderEventStatus.PENDING.name()));
        event.setOrder(order);

        orderProducer.publish(event);

        return new ResponseEntity<>("Order placed successfully", HttpStatus.CREATED);
    }
}
