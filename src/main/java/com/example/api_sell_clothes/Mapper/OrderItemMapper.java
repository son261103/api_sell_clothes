package com.example.api_sell_clothes.Mapper;

import com.example.api_sell_clothes.DTO.OrderItemsDTO;
import com.example.api_sell_clothes.Entity.OrderItems;
import com.example.api_sell_clothes.Entity.Products;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderItemMapper implements EntityMapper<OrderItems, OrderItemsDTO> {

    @Override
    public OrderItems toEntity(OrderItemsDTO dto) {
        if (dto == null) {
            return null;
        }

        OrderItems orderItem = new OrderItems();
        orderItem.setOrder_item_id(dto.getOrderId());

        // Lấy thông tin productId (id của sản phẩm)
        Products product = new Products();
        product.setProductId(dto.getProductId());
        orderItem.setProductId(product);

        orderItem.setQuantity(dto.getQuantity());
        orderItem.setPrice(dto.getPrice());
        orderItem.setCreatedAt(dto.getCreatedAt());
        orderItem.setUpdatedAt(dto.getUpdatedAt());

        return orderItem;
    }

    @Override
    public OrderItemsDTO toDto(OrderItems entity) {
        if (entity == null) {
            return null;
        }

        OrderItemsDTO orderItemDTO = new OrderItemsDTO();
        orderItemDTO.setOrderId(entity.getOrder_item_id());

        // Lấy id của sản phẩm
        if (entity.getProductId() != null) {
            orderItemDTO.setProductId(entity.getProductId().getProductId());
        }

        orderItemDTO.setQuantity(entity.getQuantity());
        orderItemDTO.setPrice(entity.getPrice());
        orderItemDTO.setCreatedAt(entity.getCreatedAt());
        orderItemDTO.setUpdatedAt(entity.getUpdatedAt());

        return orderItemDTO;
    }

    @Override
    public List<OrderItems> toEntity(List<OrderItemsDTO> dtoList) {
        if (dtoList == null) {
            return null;
        }

        return dtoList.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderItemsDTO> toDto(List<OrderItems> entityList) {
        if (entityList == null) {
            return null;
        }

        return entityList.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
