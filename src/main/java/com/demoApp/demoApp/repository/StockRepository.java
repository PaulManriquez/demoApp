package com.demoApp.demoApp.repository;

import com.demoApp.demoApp.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockRepository extends JpaRepository<Stock,Integer> {

    List<Stock> findByPurchaseId(Integer purchaseId);
}
