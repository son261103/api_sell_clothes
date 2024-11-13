package com.example.api_sell_clothes.Service;

import com.example.api_sell_clothes.DTO.PaymentsDTO;
import com.example.api_sell_clothes.Entity.Orders;
import com.example.api_sell_clothes.Entity.Payments;
import com.example.api_sell_clothes.Exception.Common.InvalidDataException;
import com.example.api_sell_clothes.Exception.Common.NotFoundException;
import com.example.api_sell_clothes.Mapper.PaymentMapper;
import com.example.api_sell_clothes.Repository.OrderRepository;
import com.example.api_sell_clothes.Repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final PaymentMapper paymentMapper;

    public PaymentsDTO createPayment(PaymentsDTO paymentDTO) {
        log.info("Tạo thanh toán mới: {}", paymentDTO);
        validatePaymentData(paymentDTO);

        Orders order = orderRepository.findById(paymentDTO.getOrderId())
                .orElseThrow(() -> new NotFoundException("Đơn hàng", "id", paymentDTO.getOrderId()));

        Payments payment = paymentMapper.toEntity(paymentDTO);
        payment.setOrderId(order);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());

        return paymentMapper.toDto(paymentRepository.save(payment));
    }

    public PaymentsDTO updatePayment(Long id, PaymentsDTO paymentDTO) {
        log.info("Cập nhật thanh toán: {}", paymentDTO);

        Payments existingPayment = paymentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Thanh toán", "id", id));

        validatePaymentData(paymentDTO);

        existingPayment.setPaymentMethodId(paymentDTO.getPaymentMethodId());
        existingPayment.setPaymentDate(paymentDTO.getPaymentDate());
        existingPayment.setAmount(paymentDTO.getAmount());
        existingPayment.setPaymentStatus(paymentDTO.getPaymentStatus());
        existingPayment.setUpdatedAt(LocalDateTime.now());

        return paymentMapper.toDto(paymentRepository.save(existingPayment));
    }

    public void deletePayment(Long id) {
        log.info("Xóa thanh toán: {}", id);

        Payments payment = paymentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Thanh toán", "id", id));

        paymentRepository.delete(payment);
    }

    @Transactional(readOnly = true)
    public PaymentsDTO getPaymentById(Long id) {
        log.info("Lấy thanh toán theo ID: {}", id);

        Payments payment = paymentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Thanh toán", "id", id));

        return paymentMapper.toDto(payment);
    }

    @Transactional(readOnly = true)
    public List<PaymentsDTO> getAllPayments() {
        log.info("Lấy tất cả thanh toán");

        return paymentMapper.toDto(paymentRepository.findAll());
    }

    @Transactional(readOnly = true)
    public List<PaymentsDTO> getPaymentsByOrderId(Long orderId) {
        log.info("Lấy tất cả thanh toán theo ID đơn hàng: {}", orderId);

        if (!orderRepository.existsById(orderId)) {
            throw new NotFoundException("Đơn hàng", "id", orderId);
        }

        List<Payments> payments = paymentRepository.findByOrderIdOrderId(orderId);
        return paymentMapper.toDto(payments);
    }

    // Các phương thức hỗ trợ
    private void validatePaymentData(PaymentsDTO paymentDTO) {
        if (paymentDTO == null) {
            throw new InvalidDataException("Dữ liệu thanh toán không được null");
        }
        if (paymentDTO.getOrderId() == null) {
            throw new InvalidDataException("ID đơn hàng không được để trống");
        }
        if (paymentDTO.getAmount() == null || paymentDTO.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidDataException("Số tiền thanh toán không hợp lệ");
        }
        if (paymentDTO.getPaymentStatus() == null || paymentDTO.getPaymentStatus().trim().isEmpty()) {
            throw new InvalidDataException("Trạng thái thanh toán không được để trống");
        }
    }
}
