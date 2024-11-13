package com.example.api_sell_clothes.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMethodsDTO {
    private Long paymentMethodId;
    private String methodName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
