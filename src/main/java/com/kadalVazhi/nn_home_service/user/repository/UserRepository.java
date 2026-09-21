package com.kadalVazhi.nn_home_service.user.repository;

import com.kadalVazhi.nn_home_service.user.domain.Role;
import com.kadalVazhi.nn_home_service.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByPhoneNumber(String phoneNumber);

    boolean existsByPhoneNumber(String phoneNumber);

    List<User> findByRole(Role role);
}
