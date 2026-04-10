package com.demoApp.demoApp.repository;

import com.demoApp.demoApp.entity.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseRepository extends JpaRepository<Purchase, Integer> {
}
