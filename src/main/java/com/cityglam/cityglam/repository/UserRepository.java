package com.cityglam.cityglam.repository;

import com.cityglam.cityglam.entity.Role;
import com.cityglam.cityglam.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.*;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    // check if email is registered for a specific role
    boolean existsByEmailAndRole(String email, Role role);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.email = :email AND u.role != 'MEMBER'")
    boolean existsByEmailForNonMembers(@Param("email") String email);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.email = :email AND u.role IN ('CA', 'MANAGER')")
    boolean existsByEmailForCAOrManager(@Param("email") String email);

    @Query("SELECT u FROM User u WHERE u.role = :role")
    List<User> findByRole(@Param("role") Role role);
}

