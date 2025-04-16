package com.pinboard.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pinboard.demo.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    
    // Métodos de conveniência que retornam User diretamente
    default User findUserByEmail(String email) {
        return findByEmail(email).orElse(null);
    }
    
    default User findUserByUsername(String username) {
        return findByUsername(username).orElse(null);
    }
}