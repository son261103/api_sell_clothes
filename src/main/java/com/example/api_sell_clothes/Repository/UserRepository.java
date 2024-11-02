package com.example.api_sell_clothes.Repository;

import com.example.api_sell_clothes.Entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {

    // Tìm người dùng theo tên đăng nhập
    Optional<Users> findByUsername(String username);

    // Tìm người dùng theo email
    Optional<Users> findByEmail(String email);

    // Kiểm tra sự tồn tại của tên người dùng
    boolean existsByUsername(String username);

    // Kiểm tra sự tồn tại của email
    boolean existsByEmail(String email);

    @Query("SELECT COUNT(u) FROM Users u JOIN u.roles r WHERE r.roleName = 'SUPER_ADMIN'")
    long countSuperAdmins();
}
