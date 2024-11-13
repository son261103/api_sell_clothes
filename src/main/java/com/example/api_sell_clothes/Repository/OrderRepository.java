package com.example.api_sell_clothes.Repository;

import com.example.api_sell_clothes.Entity.Orders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Orders, Long> {
    // Tìm kiếm theo user
    List<Orders> findByUserUserId(Long userId);
    Page<Orders> findByUserUserId(Long userId, Pageable pageable);

    // Tìm kiếm theo status
    List<Orders> findByStatus(String status);
    Page<Orders> findByStatus(String status, Pageable pageable);

    // Tìm kiếm theo khoảng thời gian
    List<Orders> findByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    Page<Orders> findByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    // Tìm kiếm theo status và userId
    List<Orders> findByStatusAndUserUserId(String status, Long userId);
    Page<Orders> findByStatusAndUserUserId(String status, Long userId, Pageable pageable);

    // Tìm kiếm theo tổng tiền
    List<Orders> findByTotalAmountGreaterThanEqual(BigDecimal amount);
    List<Orders> findByTotalAmountLessThanEqual(BigDecimal amount);
    List<Orders> findByTotalAmountBetween(BigDecimal minAmount, BigDecimal maxAmount);

    // Đếm số đơn hàng theo status
    long countByStatus(String status);
    long countByUserUserId(Long userId);

    // Kiểm tra tồn tại
    boolean existsByUserUserIdAndStatus(Long userId, String status);

    // Các truy vấn tùy chỉnh
    @Query("SELECT o FROM Orders o WHERE o.user.userId = :userId AND o.orderDate >= :startDate")
    List<Orders> findUserOrdersFromDate(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate);

    @Query("SELECT o FROM Orders o WHERE o.status = :status AND o.totalAmount >= :minAmount ORDER BY o.orderDate DESC")
    List<Orders> findOrdersByStatusAndMinAmount(@Param("status") String status, @Param("minAmount") BigDecimal minAmount);

    @Query("SELECT DISTINCT o.status FROM Orders o")
    List<String> findAllDistinctStatuses();

    @Query("SELECT o FROM Orders o WHERE " +
            "(:userId IS NULL OR o.user.userId = :userId) AND " +
            "(:status IS NULL OR o.status = :status) AND " +
            "(:startDate IS NULL OR o.orderDate >= :startDate) AND " +
            "(:endDate IS NULL OR o.orderDate <= :endDate)")
    Page<Orders> findOrdersWithFilters(
            @Param("userId") Long userId,
            @Param("status") String status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

    // Thống kê
    @Query("SELECT COUNT(o), o.status FROM Orders o GROUP BY o.status")
    List<Object[]> countOrdersByStatus();

    @Query("SELECT SUM(o.totalAmount) FROM Orders o WHERE o.status = :status")
    BigDecimal calculateTotalAmountByStatus(@Param("status") String status);

    @Query("SELECT o FROM Orders o WHERE " +
            "o.orderDate >= :startDate AND " +
            "o.orderDate <= :endDate AND " +
            "o.status = :status " +
            "ORDER BY o.totalAmount DESC")
    List<Orders> findTopOrdersByAmountInPeriod(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("status") String status,
            Pageable pageable
    );
}