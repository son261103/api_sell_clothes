package com.example.api_sell_clothes.Mapper;

import com.example.api_sell_clothes.DTO.PaymentsDTO;
import com.example.api_sell_clothes.Entity.Orders;
import com.example.api_sell_clothes.Entity.Payments;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PaymentMapper implements EntityMapper<Payments, PaymentsDTO> {

    @Override
    public Payments toEntity(PaymentsDTO dto) {
        if (dto == null) {
            return null;
        }
        Payments payments = new Payments();
        payments.setPaymentId(dto.getPaymentId());

        // Set order with a new instance of Orders having only the ID
        if (dto.getOrderId() != null) {
            Orders order = new Orders();
            order.setOrderId(dto.getOrderId());
            payments.setOrderId(order);
        }

        payments.setPaymentMethodId(dto.getPaymentMethodId());
        payments.setPaymentDate(dto.getPaymentDate());
        payments.setAmount(dto.getAmount());
        payments.setPaymentStatus(dto.getPaymentStatus());
        payments.setCreatedAt(dto.getCreatedAt());
        payments.setUpdatedAt(dto.getUpdatedAt());
        return payments;
    }

    @Override
    public PaymentsDTO toDto(Payments entity) {
        if (entity == null) {
            return null;
        }
        PaymentsDTO paymentsDTO = new PaymentsDTO();
        paymentsDTO.setPaymentId(entity.getPaymentId());
        paymentsDTO.setOrderId(entity.getOrderId() != null ? entity.getOrderId().getOrderId() : null);
        paymentsDTO.setPaymentMethodId(entity.getPaymentMethodId());
        paymentsDTO.setPaymentDate(entity.getPaymentDate());
        paymentsDTO.setAmount(entity.getAmount());
        paymentsDTO.setPaymentStatus(entity.getPaymentStatus());
        paymentsDTO.setCreatedAt(entity.getCreatedAt());
        paymentsDTO.setUpdatedAt(entity.getUpdatedAt());
        return paymentsDTO;
    }

    @Override
    public List<Payments> toEntity(List<PaymentsDTO> dtoList) {
        if (dtoList == null) {
            return null;
        }
        return dtoList.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentsDTO> toDto(List<Payments> entityList) {
        if (entityList == null) {
            return null;
        }
        return entityList.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
