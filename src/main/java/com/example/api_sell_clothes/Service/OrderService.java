package com.example.api_sell_clothes.Service;

import com.example.api_sell_clothes.DTO.OrdersDTO;
import com.example.api_sell_clothes.Entity.Orders;
import com.example.api_sell_clothes.Entity.Users;
import com.example.api_sell_clothes.Exception.Common.*;
import com.example.api_sell_clothes.Mapper.OrderMapper;
import com.example.api_sell_clothes.Repository.OrderRepository;
import com.example.api_sell_clothes.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;

    public OrdersDTO createOrder(OrdersDTO orderDTO) {
        log.info("Tạo đơn hàng mới : {}", orderDTO);
        validateOrderData(orderDTO);

        Users user = userRepository.findById(orderDTO.getUserId())
                .orElseThrow(() -> new NotFoundException("Người dùng", "id", orderDTO.getUserId()));

        Orders order = orderMapper.toEntity(orderDTO);
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        if (order.getStatus() == null || order.getStatus().trim().isEmpty()) {
            order.setStatus("pending");
        }

        return orderMapper.toDto(orderRepository.save(order));
    }

    public OrdersDTO updateOrder(Long id, OrdersDTO orderDTO) {
        log.info("Cập nhật đơn hàng : {}", orderDTO);

        Orders existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Đơn hàng", "id", id));

        validateOrderData(orderDTO);
        validateOrderStatus(orderDTO.getStatus());
        validateStatusTransition(existingOrder.getStatus(), orderDTO.getStatus());

        // Giữ nguyên một số thông tin quan trọng
        LocalDateTime createdAt = existingOrder.getCreatedAt();
        Users user = existingOrder.getUser();

        // Cập nhật thông tin đơn hàng
        existingOrder.setTotalAmount(orderDTO.getTotalAmount());
        existingOrder.setStatus(orderDTO.getStatus());
        existingOrder.setUpdatedAt(LocalDateTime.now());

        return orderMapper.toDto(orderRepository.save(existingOrder));
    }

    public void deleteOrder(Long id) {
        log.info("Xóa đơn hàng : {}", id);

        Orders order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Đơn hàng", "id", id));

        if (!canDeleteOrder(order)) {
            throw new ResourceInUseException("Đơn hàng", "không thể xóa đơn hàng đã được xử lý");
        }

        orderRepository.delete(order);
    }

    @Transactional(readOnly = true)
    public Page<OrdersDTO> getAllOrders(Pageable pageable) {
        log.info("Lấy tất cả đơn hàng với phân trang");
        return orderRepository.findAll(pageable).map(orderMapper::toDto);
    }

    @Transactional(readOnly = true)
    public OrdersDTO getOrderById(Long id) {
        log.info("Lấy đơn hàng theo id : {}", id);
        return orderMapper.toDto(orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Đơn hàng", "id", id)));
    }

    @Transactional(readOnly = true)
    public Page<OrdersDTO> getOrdersByUserId(Long userId, Pageable pageable) {
        log.info("Lấy đơn hàng theo user id : {}", userId);

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Người dùng", "id", userId);
        }

        return orderRepository.findByUserUserId(userId, pageable).map(orderMapper::toDto);
    }

    public OrdersDTO updateOrderStatus(Long id, String status) {
        log.info("Cập nhật trạng thái đơn hàng {} thành {}", id, status);

        Orders order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Đơn hàng", "id", id));

        validateOrderStatus(status);
        validateStatusTransition(order.getStatus(), status);

        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());

        return orderMapper.toDto(orderRepository.save(order));
    }

    @Transactional(readOnly = true)
    public Page<OrdersDTO> searchOrders(
            Long userId,
            String status,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable
    ) {
        log.info("Tìm kiếm đơn hàng với các filter");
        return orderRepository.findOrdersWithFilters(userId, status, startDate, endDate, pageable)
                .map(orderMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<OrdersDTO> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Lấy đơn hàng trong khoảng thời gian từ {} đến {}", startDate, endDate);
        return orderMapper.toDto(orderRepository.findByOrderDateBetween(startDate, endDate));
    }

    @Transactional(readOnly = true)
    public List<OrdersDTO> getOrdersByAmountRange(BigDecimal minAmount, BigDecimal maxAmount) {
        log.info("Lấy đơn hàng trong khoảng giá từ {} đến {}", minAmount, maxAmount);
        return orderMapper.toDto(orderRepository.findByTotalAmountBetween(minAmount, maxAmount));
    }

    @Transactional(readOnly = true)
    public Map<String, Long> getOrderStatistics() {
        log.info("Lấy thống kê đơn hàng theo trạng thái");
        List<Object[]> statistics = orderRepository.countOrdersByStatus();
        return statistics.stream()
                .collect(Collectors.toMap(
                        row -> (String) row[1],
                        row -> (Long) row[0]
                ));
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalAmountByStatus(String status) {
        log.info("Tính tổng giá trị đơn hàng theo trạng thái: {}", status);
        validateOrderStatus(status);
        return orderRepository.calculateTotalAmountByStatus(status);
    }

    @Transactional(readOnly = true)
    public List<OrdersDTO> getTopOrdersByAmount(LocalDateTime startDate, LocalDateTime endDate, String status, int limit) {
        log.info("Lấy top {} đơn hàng có giá trị cao nhất", limit);
        return orderMapper.toDto(orderRepository.findTopOrdersByAmountInPeriod(
                startDate, endDate, status, Pageable.ofSize(limit)));
    }

    // Các phương thức hỗ trợ
    private void validateOrderData(OrdersDTO orderDTO) {
        if (orderDTO == null) {
            throw new InvalidDataException("Dữ liệu đơn hàng không được null");
        }
        if (orderDTO.getUserId() == null) {
            throw new InvalidDataException("ID người dùng không được để trống");
        }
        if (orderDTO.getTotalAmount() == null || orderDTO.getTotalAmount().signum() < 0) {
            throw new InvalidDataException("Tổng tiền không hợp lệ");
        }
    }

    private void validateOrderStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            throw new InvalidDataException("Trạng thái đơn hàng không được để trống");
        }

        List<String> validStatuses = List.of("pending", "processing", "shipped", "delivered", "cancelled");
        if (!validStatuses.contains(status.toLowerCase())) {
            throw new InvalidDataException("Trạng thái đơn hàng không hợp lệ");
        }
    }

    private void validateStatusTransition(String currentStatus, String newStatus) {
        if ("delivered".equalsIgnoreCase(currentStatus) || "cancelled".equalsIgnoreCase(currentStatus)) {
            throw new InvalidStatusTransitionException("Không thể thay đổi trạng thái của đơn hàng đã hoàn thành hoặc đã hủy");
        }

        // Kiểm tra luồng trạng thái hợp lệ
        Map<String, List<String>> validTransitions = Map.of(
                "pending", List.of("processing", "cancelled"),
                "processing", List.of("shipped", "cancelled"),
                "shipped", List.of("delivered", "cancelled")
        );

        List<String> allowedTransitions = validTransitions.get(currentStatus.toLowerCase());
        if (allowedTransitions == null || !allowedTransitions.contains(newStatus.toLowerCase())) {
            throw new InvalidStatusTransitionException(
                    String.format("Không thể chuyển trạng thái từ '%s' sang '%s'", currentStatus, newStatus)
            );
        }
    }

    private boolean canDeleteOrder(Orders order) {
        return "pending".equalsIgnoreCase(order.getStatus()) ||
                "cancelled".equalsIgnoreCase(order.getStatus());
    }
}