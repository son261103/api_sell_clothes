package com.example.api_sell_clothes.Repository;

import com.example.api_sell_clothes.Entity.PaymentMethods;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethods, Long> {

}
