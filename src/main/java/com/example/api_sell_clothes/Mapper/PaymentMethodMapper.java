package com.example.api_sell_clothes.Mapper;

import com.example.api_sell_clothes.DTO.PaymentMethodsDTO;
import com.example.api_sell_clothes.Entity.PaymentMethods;
<<<<<<< HEAD
import org.springframework.stereotype.Component;
=======
>>>>>>> 0c41bd17ae39e748e50a1ad927fd7f5ab750a971

import java.util.List;
import java.util.stream.Collectors;

<<<<<<< HEAD

@Component
=======
>>>>>>> 0c41bd17ae39e748e50a1ad927fd7f5ab750a971
public class PaymentMethodMapper implements EntityMapper<PaymentMethods, PaymentMethodsDTO> {
    @Override
    public PaymentMethods toEntity(PaymentMethodsDTO dto) {
        if (dto == null) {
            return null;
        }
<<<<<<< HEAD
        PaymentMethods paymentMethods = PaymentMethods.builder()
                .paymentMethodId(dto.getPaymentMethodId())
                .methodName(dto.getMethodName())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .build();
        return paymentMethods;
=======
        return PaymentMethods.builder()
                .paymentMethodId(dto.getPaymentMethodId())
                .methodName(dto.getMethodName())
                .build();
>>>>>>> 0c41bd17ae39e748e50a1ad927fd7f5ab750a971
    }

    @Override
    public PaymentMethodsDTO toDto(PaymentMethods entity) {
        if (entity == null) {
            return null;
        }
<<<<<<< HEAD
        PaymentMethodsDTO paymentMethodsDTO = PaymentMethodsDTO.builder()
                .paymentMethodId(entity.getPaymentMethodId())
                .methodName(entity.getMethodName())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
        return paymentMethodsDTO;
=======
        return PaymentMethodsDTO.builder()
                .paymentMethodId(entity.getPaymentMethodId())
                .methodName(entity.getMethodName())
                .build();
>>>>>>> 0c41bd17ae39e748e50a1ad927fd7f5ab750a971
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
