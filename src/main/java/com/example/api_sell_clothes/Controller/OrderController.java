package com.example.api_sell_clothes.Controller;

import com.example.api_sell_clothes.DTO.OrdersDTO;
import com.example.api_sell_clothes.Service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrdersDTO> createOrder(@Valid @RequestBody OrdersDTO orderDTO) {
        OrdersDTO createdOrder = orderService.createOrder(orderDTO);
        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<OrdersDTO> updateOrder(
            @PathVariable Long id,
            @Valid @RequestBody OrdersDTO orderDTO) {
        OrdersDTO updatedOrder = orderService.updateOrder(id, orderDTO);
        return ResponseEntity.ok(updatedOrder);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Page<OrdersDTO>> getAllOrders(Pageable pageable) {
        return ResponseEntity.ok(orderService.getAllOrders(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrdersDTO> getOrderById(@PathVariable Long id) {
        OrdersDTO order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<OrdersDTO>> getOrdersByUserId(@PathVariable Long userId, Pageable pageable) {
        return ResponseEntity.ok(orderService.getOrdersByUserId(userId, pageable));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<OrdersDTO> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        OrdersDTO updatedOrder = orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok(updatedOrder);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Page<OrdersDTO>> searchOrders(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate,
            Pageable pageable) {
        Page<OrdersDTO> orders = orderService.searchOrders(userId, status, startDate, endDate, pageable);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/date-range")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<OrdersDTO>> getOrdersByDateRange(
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        List<OrdersDTO> orders = orderService.getOrdersByDateRange(startDate, endDate);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/amount-range")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<OrdersDTO>> getOrdersByAmountRange(
            @RequestParam BigDecimal minAmount,
            @RequestParam BigDecimal maxAmount) {
        List<OrdersDTO> orders = orderService.getOrdersByAmountRange(minAmount, maxAmount);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Map<String, Long>> getOrderStatistics() {
        Map<String, Long> statistics = orderService.getOrderStatistics();
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/total-amount")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<BigDecimal> getTotalAmountByStatus(@RequestParam String status) {
        BigDecimal totalAmount = orderService.getTotalAmountByStatus(status);
        return ResponseEntity.ok(totalAmount);
    }

    @GetMapping("/top-orders")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<OrdersDTO>> getTopOrdersByAmount(
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate,
            @RequestParam String status,
            @RequestParam int limit) {
        List<OrdersDTO> topOrders = orderService.getTopOrdersByAmount(startDate, endDate, status, limit);
        return ResponseEntity.ok(topOrders);
    }
}
