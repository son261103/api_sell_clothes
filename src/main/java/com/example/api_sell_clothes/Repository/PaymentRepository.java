package com.example.api_sell_clothes.Repository;

import com.example.api_sell_clothes.Entity.Payments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payments, Long> {

    // Tìm các thanh toán liên quan đến một đơn hàng (orderId)
    List<Payments> findByOrderIdOrderId(Long orderId);

}
