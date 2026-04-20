package com.demoApp.demoApp.repository;

import com.demoApp.demoApp.entity.SaleDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SaleDetailRepository extends JpaRepository<SaleDetail,Integer> {

    List<SaleDetail> findBySaleId(Integer saleId);

}
