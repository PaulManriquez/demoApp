package com.demoApp.demoApp.service;

import com.demoApp.demoApp.entity.Provider;
import com.demoApp.demoApp.entity.Purchase;
import com.demoApp.demoApp.entity.User;
import com.demoApp.demoApp.model.Message;
import com.demoApp.demoApp.repository.PurchaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;

    private final UserService userService;

    private final ProviderService providerService;

    @Autowired
    public PurchaseService(PurchaseRepository purchaseRepository, UserService userService,
                           ProviderService providerService) {
        this.userService = userService;
        this.purchaseRepository = purchaseRepository;
        this.providerService = providerService;
    }

    public List<Purchase> getAllPurchases (){
        return purchaseRepository.findAll();
    }

    public Message saveCreatePurchase(Purchase purchase) {

        // Verify purchase do not exist already
        if (purchase.getId() != null){
            return new Message("La compra ya existe", false);
        }

        // Verify provider exist
        if (purchase.getProvider().getId() == null){
            return new Message("El proveedor es obligatorio", false);
        }

        // Get the authenticated user. This service now expects a valid authenticated user or an exception.
        User user = userService.getCurrentlyAuthenticatedUser();

        // Get provider
        Provider provider = providerService.getProviderById(purchase.getProvider().getId());

        // Initialize creation-only fields for a new purchase.
        purchase.setDate(Instant.now());
        purchase.setCreatedAt(Instant.now());
        purchase.setUser(user);
        purchase.setProvider(provider);

        // Save the purchase
        purchaseRepository.save(purchase);

        return new Message("Compra guardada con exito", true);
    }
}
