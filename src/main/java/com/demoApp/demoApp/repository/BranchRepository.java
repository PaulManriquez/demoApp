package com.demoApp.demoApp.repository;

import com.demoApp.demoApp.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Integer> {

    public Optional<Branch> findById(int id);
}
