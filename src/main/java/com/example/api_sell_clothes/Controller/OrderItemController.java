package com.example.api_sell_clothes.Controller;

import com.example.api_sell_clothes.DTO.OrderItemsDTO;
import com.example.api_sell_clothes.Service.OrderItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders/{orderId}/items")
@RequiredArgsConstructor
public class OrderItemController {

    private final OrderItemService orderItemService;

    // Tạo mới một OrderItem cho một Order đã chỉ định
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<OrderItemsDTO> createOrderItem(
            @PathVariable Long orderId,
            @Valid @RequestBody OrderItemsDTO orderItemDTO) {
        OrderItemsDTO createdOrderItem = orderItemService.createOrderItem(orderId, orderItemDTO);
        return new ResponseEntity<>(createdOrderItem, HttpStatus.CREATED);
    }

    // Cập nhật một OrderItem theo orderId và productId
    @PutMapping("/{productId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<OrderItemsDTO> updateOrderItem(
            @PathVariable Long orderId,
            @PathVariable Long productId,
            @Valid @RequestBody OrderItemsDTO orderItemDTO) {
        OrderItemsDTO updatedOrderItem = orderItemService.updateOrderItem(orderId, productId, orderItemDTO);
        return ResponseEntity.ok(updatedOrderItem);
    }

    // Xóa một OrderItem theo orderId và productId
    @DeleteMapping("/{productId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Void> deleteOrderItem(
            @PathVariable Long orderId,
            @PathVariable Long productId) {
        orderItemService.deleteOrderItem(orderId, productId);
        return ResponseEntity.noContent().build();
    }

    // Lấy tất cả OrderItems của một Order
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<List<OrderItemsDTO>> getOrderItemsByOrderId(@PathVariable Long orderId) {
        List<OrderItemsDTO> orderItems = orderItemService.getOrderItemsByOrderId(orderId);
        return ResponseEntity.ok(orderItems);
    }

    // Lấy một OrderItem cụ thể theo orderId và productId
    @GetMapping("/{productId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<OrderItemsDTO> getOrderItemByOrderIdAndProductId(
            @PathVariable Long orderId,
            @PathVariable Long productId) {
        OrderItemsDTO orderItem = orderItemService.getOrderItemByOrderIdAndProductId(orderId, productId);
        return ResponseEntity.ok(orderItem);
    }
}
