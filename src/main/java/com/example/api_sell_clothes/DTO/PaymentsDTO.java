package com.example.api_sell_clothes.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentsDTO {
    private Long paymentId;
    private Long orderId;
    private Long paymentMethodId;
    private LocalDateTime paymentDate;
    private BigDecimal amount;
    private String paymentStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
