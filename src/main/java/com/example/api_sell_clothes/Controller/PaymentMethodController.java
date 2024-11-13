package com.example.api_sell_clothes.Controller;

import com.example.api_sell_clothes.DTO.PaymentMethodsDTO;
import com.example.api_sell_clothes.Service.PaymentMethodService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/payment-methods")
@RequiredArgsConstructor
public class PaymentMethodController {

    private final PaymentMethodService paymentMethodService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<PaymentMethodsDTO> createPaymentMethod(@Valid @RequestBody PaymentMethodsDTO paymentMethodDTO) {
        try {
            return new ResponseEntity<>(paymentMethodService.createPaymentMethod(paymentMethodDTO), HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Không thể tạo phương thức thanh toán: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<PaymentMethodsDTO> updatePaymentMethod(
            @PathVariable Long id,
            @Valid @RequestBody PaymentMethodsDTO paymentMethodDTO) {
        try {
            return ResponseEntity.ok(paymentMethodService.updatePaymentMethod(id, paymentMethodDTO));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Không thể cập nhật phương thức thanh toán: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Void> deletePaymentMethod(@PathVariable Long id) {
        paymentMethodService.deletePaymentMethod(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<PaymentMethodsDTO>> getAllPaymentMethods() {
        return ResponseEntity.ok(paymentMethodService.getAllPaymentMethods());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentMethodsDTO> getPaymentMethodById(@PathVariable Long id) {
        return ResponseEntity.ok(paymentMethodService.getPaymentMethodById(id));
    }
}