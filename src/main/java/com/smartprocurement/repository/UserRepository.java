package com.smartprocurement.repository;

import com.smartprocurement.entity.Role;
import com.smartprocurement.entity.User;
import com.smartprocurement.entity.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByPhone(String phone);
    boolean existsByPhone(String phone);
    List<User> findByRoleAndStatus(Role role, UserStatus status);
    List<User> findByRole(Role role);
}
