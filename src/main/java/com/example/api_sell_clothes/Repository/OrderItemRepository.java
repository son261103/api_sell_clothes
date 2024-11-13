package com.example.api_sell_clothes.Repository;

import com.example.api_sell_clothes.Entity.OrderItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItems, Long> {
    // Tìm tất cả các OrderItems theo orderId
    @Query("SELECT oi FROM OrderItems oi WHERE oi.orderId.orderId = :orderId")
    List<OrderItems> findByOrderId(@Param("orderId") Long orderId);

    // Tìm một OrderItem dựa trên orderId và productId
    @Query("SELECT oi FROM OrderItems oi WHERE oi.orderId.orderId = :orderId AND oi.productId.productId = :productId")
    Optional<OrderItems> findByOrderIdAndProductId(@Param("orderId") Long orderId, @Param("productId") Long productId);
}