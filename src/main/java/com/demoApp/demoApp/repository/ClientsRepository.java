package com.demoApp.demoApp.repository;

import com.demoApp.demoApp.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientsRepository extends JpaRepository<Client,Integer> {

    List<Client> findAllByActiveTrueOrderByFirstNameAscLastNameAsc();
}
