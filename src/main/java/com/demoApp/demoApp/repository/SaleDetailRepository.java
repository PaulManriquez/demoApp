package com.demoApp.demoApp.repository;

import com.demoApp.demoApp.entity.SaleDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SaleDetailRepository extends JpaRepository<SaleDetail,Integer> {

    @Query(value = "SELECT * FROM sale_details where sale_id = :saleId", nativeQuery = true)
    List<SaleDetail> getAllSaleDetailsById(@Param("saleId") Integer saleId);

}
