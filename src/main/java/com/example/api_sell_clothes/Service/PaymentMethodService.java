package com.example.api_sell_clothes.Service;

import com.example.api_sell_clothes.DTO.PaymentMethodsDTO;
import com.example.api_sell_clothes.Entity.PaymentMethods;
import com.example.api_sell_clothes.Exception.Common.*;
import com.example.api_sell_clothes.Mapper.PaymentMethodMapper;
import com.example.api_sell_clothes.Repository.PaymentMethodRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PaymentMethodService {
    private final PaymentMethodRepository paymentMethodRepository;
    private final PaymentMethodMapper paymentMethodMapper;

    public PaymentMethodsDTO createPaymentMethod(PaymentMethodsDTO paymentMethodDTO) {
        log.info("Tạo phương thức thanh toán mới : {}", paymentMethodDTO);
        validatePaymentMethodData(paymentMethodDTO);

        // Set thời gian
        paymentMethodDTO.setCreatedAt(LocalDateTime.now());
        paymentMethodDTO.setUpdatedAt(LocalDateTime.now());

        // Chuyển đổi DTO thành entity và lưu
        PaymentMethods paymentMethod = paymentMethodMapper.toEntity(paymentMethodDTO);
        return paymentMethodMapper.toDto(paymentMethodRepository.save(paymentMethod));
    }

    public PaymentMethodsDTO updatePaymentMethod(Long id, PaymentMethodsDTO paymentMethodDTO) {
        log.info("Cập nhật phương thức thanh toán : {}", paymentMethodDTO);

        PaymentMethods existingPaymentMethod = paymentMethodRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Phương thức thanh toán", "id", id));

        validatePaymentMethodData(paymentMethodDTO);

        LocalDateTime createdAt = existingPaymentMethod.getCreatedAt();

        // Cập nhật thông tin
        existingPaymentMethod.setMethodName(paymentMethodDTO.getMethodName());
        existingPaymentMethod.setCreatedAt(createdAt); // Giữ nguyên thời gian tạo
        existingPaymentMethod.setUpdatedAt(LocalDateTime.now());

        return paymentMethodMapper.toDto(paymentMethodRepository.save(existingPaymentMethod));
    }

    public void deletePaymentMethod(Long id) {
        log.info("Xóa phương thức thanh toán : {}", id);

        PaymentMethods paymentMethod = paymentMethodRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Phương thức thanh toán", "id", id));

        // Kiểm tra xem phương thức thanh toán có đang được sử dụng không
        if (isPaymentMethodInUse(id)) {
            throw new ResourceInUseException("Phương thức thanh toán", "đang được sử dụng trong đơn hàng");
        }

        paymentMethodRepository.delete(paymentMethod);
    }

    @Transactional(readOnly = true)
    public List<PaymentMethodsDTO> getAllPaymentMethods() {
        log.info("Lấy tất cả phương thức thanh toán");
        return paymentMethodMapper.toDto(paymentMethodRepository.findAll());
    }

    @Transactional(readOnly = true)
    public PaymentMethodsDTO getPaymentMethodById(Long id) {
        log.info("Lấy phương thức thanh toán theo id : {}", id);
        return paymentMethodMapper.toDto(paymentMethodRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Phương thức thanh toán", "id", id)));
    }

    // Các phương thức hỗ trợ
    private void validatePaymentMethodData(PaymentMethodsDTO paymentMethodDTO) {
        if (paymentMethodDTO == null) {
            throw new InvalidDataException("Dữ liệu phương thức thanh toán không được null");
        }
        if (paymentMethodDTO.getMethodName() == null || paymentMethodDTO.getMethodName().trim().isEmpty()) {
            throw new InvalidDataException("Tên phương thức thanh toán không được để trống");
        }
    }

    private boolean isPaymentMethodInUse(Long paymentMethodId) {
        // Implement logic kiểm tra phương thức thanh toán có trong đơn hàng
        return false;
    }
}