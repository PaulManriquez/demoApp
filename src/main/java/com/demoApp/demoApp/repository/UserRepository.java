package com.demoApp.demoApp.repository;

import com.demoApp.demoApp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
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

    @Query("""
       SELECT DISTINCT u
       FROM User u
       LEFT JOIN FETCH u.roles
       ORDER BY u.id DESC
       """)
    List<User> findAllWithRoles();

    @Query("""
       SELECT DISTINCT u
       FROM User u
       JOIN u.roles r
       WHERE r.name = 'TECHNICIAN'
         AND u.status = true
       ORDER BY u.name ASC
       """)
    List<User> findAllTechnicians();
}
