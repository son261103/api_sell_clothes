package com.example.api_sell_clothes.Repository;

import com.example.api_sell_clothes.Entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {

    // Tìm người dùng theo tên đăng nhập
    Optional<Users> findByUsername(String username);

    // Tìm người dùng theo email (nếu cần)
    Optional<Users> findByEmail(String email);

}
