package com.berkay.usermanagement.repository;

import com.berkay.usermanagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByIsSellerApplication(boolean isSellerApplication);
    List<User> findByIsIdApplication(boolean isIdApplication);
}
