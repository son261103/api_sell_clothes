package com.example.api_sell_clothes.Controller;

import com.example.api_sell_clothes.DTO.PaymentsDTO;
import com.example.api_sell_clothes.Service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<PaymentsDTO> createPayment(@Valid @RequestBody PaymentsDTO paymentDTO) {
        PaymentsDTO createdPayment = paymentService.createPayment(paymentDTO);
        return new ResponseEntity<>(createdPayment, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<PaymentsDTO> updatePayment(
            @PathVariable Long id,
            @Valid @RequestBody PaymentsDTO paymentDTO) {
        PaymentsDTO updatedPayment = paymentService.updatePayment(id, paymentDTO);
        return ResponseEntity.ok(updatedPayment);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) {
        paymentService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<PaymentsDTO> getPaymentById(@PathVariable Long id) {
        PaymentsDTO payment = paymentService.getPaymentById(id);
        return ResponseEntity.ok(payment);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<PaymentsDTO>> getAllPayments() {
        List<PaymentsDTO> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/order/{orderId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<PaymentsDTO>> getPaymentsByOrderId(@PathVariable Long orderId) {
        List<PaymentsDTO> payments = paymentService.getPaymentsByOrderId(orderId);
        return ResponseEntity.ok(payments);
    }
}
