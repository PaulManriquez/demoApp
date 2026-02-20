package com.demoApp.demoApp.repository;

import com.demoApp.demoApp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    // Find a user by username
    Optional<User> findByUsername(String username);

    @Query("""
       SELECT u
       FROM User u
       LEFT JOIN FETCH u.roles
       WHERE u.username = :identifier
          OR u.email = :identifier
       """)
    Optional<User> findByUsernameOrEmailWithRoles(@Param("identifier") String identifier);
}
