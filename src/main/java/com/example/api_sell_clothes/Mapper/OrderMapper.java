package com.example.api_sell_clothes.Mapper;

import com.example.api_sell_clothes.DTO.OrdersDTO;
import com.example.api_sell_clothes.Entity.Orders;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper implements EntityMapper<Orders, OrdersDTO> {
    @Override
    public Orders toEntity(OrdersDTO dto) {
        if (dto == null) {
            return null;
        }
        Orders orders = Orders.builder()
                .orderId(dto.getOrder_item_id())
                .orderDate(dto.getOrderDate())
                .status(dto.getStatus())
                .totalAmount(dto.getTotalAmount())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .build();
        return orders;
    }

    @Override
    public OrdersDTO toDto(Orders entity) {
        if (entity == null) {
            return null;
        }
        OrdersDTO ordersDTO = OrdersDTO.builder()
                .order_item_id(entity.getOrderId())
                .userId(entity.getUser() != null ? entity.getUser().getUserId() : null)
                .username(entity.getUser() != null ? entity.getUser().getUsername() : null)
                .orderDate(entity.getOrderDate())
                .status(entity.getStatus())
                .totalAmount(entity.getTotalAmount())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
        return ordersDTO;
    }

    @Override
    public List<Orders> toEntity(List<OrdersDTO> dtoList) {
        if (dtoList == null) {
            return null;
        }
        return dtoList.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrdersDTO> toDto(List<Orders> entityList) {
        if (entityList == null) {
            return null;
        }
        return entityList.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}