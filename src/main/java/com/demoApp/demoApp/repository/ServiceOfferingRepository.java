package com.demoApp.demoApp.repository;

import com.demoApp.demoApp.entity.ServiceOffering;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceOfferingRepository extends JpaRepository<ServiceOffering, Integer> {
    List<ServiceOffering> findAllByOrderByIdDesc();
    List<ServiceOffering> findAllByVisibleTrueOrderByNameAsc();
}

