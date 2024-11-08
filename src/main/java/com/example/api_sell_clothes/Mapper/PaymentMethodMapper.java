package com.example.api_sell_clothes.Mapper;

import com.example.api_sell_clothes.DTO.PaymentMethodsDTO;
import com.example.api_sell_clothes.Entity.PaymentMethods;

import java.util.List;
import java.util.stream.Collectors;

public class PaymentMethodMapper implements EntityMapper<PaymentMethods, PaymentMethodsDTO> {
    @Override
    public PaymentMethods toEntity(PaymentMethodsDTO dto) {
        if (dto == null) {
            return null;
        }
        return PaymentMethods.builder()
                .paymentMethodId(dto.getPaymentMethodId())
                .methodName(dto.getMethodName())
                .build();
    }

    @Override
    public PaymentMethodsDTO toDto(PaymentMethods entity) {
        if (entity == null) {
            return null;
        }
        return PaymentMethodsDTO.builder()
                .paymentMethodId(entity.getPaymentMethodId())
                .methodName(entity.getMethodName())
                .build();
    }

    @Override
    public List<PaymentMethods> toEntity(List<PaymentMethodsDTO> Dto) {
        if (Dto == null) {
            return null;
        }
        return Dto.stream().map(this::toEntity).collect(Collectors.toList());
    }

    @Override
    public List<PaymentMethodsDTO> toDto(List<PaymentMethods> entity) {
        if (entity == null) {
            return null;
        }
        return entity.stream().map(this::toDto).collect(Collectors.toList());
    }
}
