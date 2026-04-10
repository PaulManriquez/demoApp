package com.demoApp.demoApp.service;

import com.demoApp.demoApp.entity.Provider;
import com.demoApp.demoApp.repository.ProviderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProviderService {


    private final ProviderRepository providerRepository;

    @Autowired
    public ProviderService(ProviderRepository providerRepository) {
        this.providerRepository = providerRepository;
    }

    public List<Provider> getAllProviders(){
        return providerRepository.findAll();
    }

    public Provider getProviderById(Integer id) {
        return providerRepository.findById(id).orElse(null);
    }


}
