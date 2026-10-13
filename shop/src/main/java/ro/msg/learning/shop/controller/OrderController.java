package ro.msg.learning.shop.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.msg.learning.shop.dto.request.CreateOrderRequest;
import ro.msg.learning.shop.dto.response.OrderResponse;
import ro.msg.learning.shop.mapper.OrderMapper;
import ro.msg.learning.shop.model.Order;
import ro.msg.learning.shop.service.OrderService;

import java.net.URI;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    private final OrderMapper mapper;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody CreateOrderRequest request) {
        Order order = mapper.toModel(request);

        Order createdOrder = orderService.createOrder(order);

        return ResponseEntity.created(URI.create("/api/orders/" + createdOrder.getId())).body(mapper.toDto(createdOrder));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable UUID id) {
        Order order = orderService.getOrderById(id);

        return ResponseEntity.ok(mapper.toDto(order));
    }
}
