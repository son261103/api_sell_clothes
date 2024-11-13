package com.example.api_sell_clothes.Service;

import com.example.api_sell_clothes.DTO.OrderItemsDTO;
import com.example.api_sell_clothes.Entity.OrderItems;
import com.example.api_sell_clothes.Entity.Orders;
import com.example.api_sell_clothes.Exception.Common.NotFoundException;
import com.example.api_sell_clothes.Mapper.OrderItemMapper;
import com.example.api_sell_clothes.Repository.OrderItemRepository;
import com.example.api_sell_clothes.Repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final OrderItemMapper orderItemMapper;

    public OrderItemsDTO createOrderItem(Long orderId, OrderItemsDTO orderItemDTO) {
        log.info("Creating order item for order ID {}: {}", orderId, orderItemDTO);

        // Kiểm tra xem đơn hàng có tồn tại hay không
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order", "id", orderId));

        // Chuyển đổi từ DTO sang entity và thiết lập các thuộc tính
        OrderItems orderItem = orderItemMapper.toEntity(orderItemDTO);
        orderItem.setOrder_item_id(order.getOrderId());  // Liên kết order item với order
        orderItem.setCreatedAt(orderItemDTO.getCreatedAt());
        orderItem.setUpdatedAt(orderItemDTO.getUpdatedAt());

        return orderItemMapper.toDto(orderItemRepository.save(orderItem));
    }

    public OrderItemsDTO updateOrderItem(Long orderId, Long productId, OrderItemsDTO orderItemDTO) {
        log.info("Updating order item with order ID {} and product ID {}: {}", orderId, productId, orderItemDTO);

        // Lấy `OrderItems` từ cơ sở dữ liệu và kiểm tra nếu không tìm thấy
        OrderItems existingOrderItem = orderItemRepository.findByOrderIdAndProductId(orderId, productId)
                .orElseThrow(() -> new NotFoundException("Order Item", "orderId and productId", orderId + ", " + productId));

        // Cập nhật các thông tin mới từ `orderItemDTO`
        existingOrderItem.setQuantity(orderItemDTO.getQuantity());
        existingOrderItem.setPrice(orderItemDTO.getPrice());
        existingOrderItem.setUpdatedAt(orderItemDTO.getUpdatedAt());

        return orderItemMapper.toDto(orderItemRepository.save(existingOrderItem));
    }

    public void deleteOrderItem(Long orderId, Long productId) {
        log.info("Deleting order item with order ID {} and product ID {}", orderId, productId);

        // Lấy `OrderItems` từ cơ sở dữ liệu và kiểm tra nếu không tìm thấy
        OrderItems orderItem = orderItemRepository.findByOrderIdAndProductId(orderId, productId)
                .orElseThrow(() -> new NotFoundException("Order Item", "orderId and productId", orderId + ", " + productId));

        orderItemRepository.delete(orderItem);
    }

    @Transactional(readOnly = true)
    public List<OrderItemsDTO> getOrderItemsByOrderId(Long orderId) {
        log.info("Fetching all order items for order ID {}", orderId);

        // Lấy tất cả các mục đơn hàng của đơn hàng đã cho
        List<OrderItems> orderItems = orderItemRepository.findByOrderId(orderId);
        return orderItemMapper.toDto(orderItems);
    }

    @Transactional(readOnly = true)
    public OrderItemsDTO getOrderItemByOrderIdAndProductId(Long orderId, Long productId) {
        log.info("Fetching order item with order ID {} and product ID {}", orderId, productId);

        // Lấy một mục đơn hàng cụ thể từ cơ sở dữ liệu
        OrderItems orderItem = orderItemRepository.findByOrderIdAndProductId(orderId, productId)
                .orElseThrow(() -> new NotFoundException("Order Item", "orderId and productId", orderId + ", " + productId));

        return orderItemMapper.toDto(orderItem);
    }
}
