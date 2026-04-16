package com.demoApp.demoApp.repository;

import com.demoApp.demoApp.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface StockRepository extends JpaRepository<Stock,Integer> {

    List<Stock> findByPurchaseId(Integer purchaseId);

    // COALESCE : Ensure that at least you have a 0 if you have nulls values
    @Query("SELECT COALESCE(SUM(s.salePrice), 0) FROM Stock s WHERE s.purchase.id = :purchaseId")
    BigDecimal sumSalePriceByPurchaseId(@Param("purchaseId") Integer purchaseId);
}
