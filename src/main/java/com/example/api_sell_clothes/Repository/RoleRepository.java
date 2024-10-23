package com.example.api_sell_clothes.Repository;

import com.example.api_sell_clothes.Entity.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Roles, Long> {
    Optional<Roles> findByRoleName(String roleName);

    boolean existsByRoleName(String roleName);
}
