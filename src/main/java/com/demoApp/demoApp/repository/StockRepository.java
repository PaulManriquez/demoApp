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

    @Query(value = """
            SELECT *
            FROM stock
            WHERE product_id = :productID
              AND sale_id IS NULL
            LIMIT :findNProductsAvailable
            """, nativeQuery = true)
    List<Stock> findAvailableStockByProductId
            (@Param("productID") Integer productID,
             @Param("findNProductsAvailable") Integer findNProductsAvailable);

    @Query(value = "SELECT * FROM stock WHERE sale_id IS NULL", nativeQuery = true)
    List<Stock> getAllAvailableStock();

    @Query(value = "SELECT product_id, COUNT(*) FROM stock WHERE sale_id IS NULL GROUP BY product_id", nativeQuery = true)
    List<Object[]> getAvailableStockByProductIdAndQuantity();
}
