package com.demoApp.demoApp.service;

import com.demoApp.demoApp.entity.ServiceOffering;
import com.demoApp.demoApp.entity.User;
import com.demoApp.demoApp.model.Message;
import com.demoApp.demoApp.repository.ServiceOfferingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceOfferingService {

    private final ServiceOfferingRepository serviceOfferingRepository;
    private final UserService userService;

    @Autowired
    public ServiceOfferingService(ServiceOfferingRepository serviceOfferingRepository, UserService userService) {
        this.serviceOfferingRepository = serviceOfferingRepository;
        this.userService = userService;
    }

    public List<ServiceOffering> getAllServices() {
        return serviceOfferingRepository.findAllByOrderByIdDesc();
    }

    public List<ServiceOffering> getVisibleServicesForSelector() {
        return serviceOfferingRepository.findAllByVisibleTrueOrderByNameAsc();
    }

    public Message createService(ServiceOffering serviceOffering) {
        if (serviceOffering.getId() != null) {
            return new Message("El servicio ya existe", false);
        }

        User user = userService.getCurrentlyAuthenticatedUser();
        serviceOffering.setUser(user);
        serviceOfferingRepository.save(serviceOffering);
        return new Message("Servicio guardado con exito", true);
    }

    public Message updateService(ServiceOffering serviceOffering) {
        if (serviceOffering.getId() == null) {
            return new Message("El servicio no existe", false);
        }

        ServiceOffering existing = serviceOfferingRepository.findById(serviceOffering.getId())
                .orElseThrow(() -> new IllegalArgumentException("El servicio no existe"));

        existing.setName(serviceOffering.getName());
        existing.setCategory(serviceOffering.getCategory());
        existing.setDescription(serviceOffering.getDescription());
        existing.setDurationMinutes(serviceOffering.getDurationMinutes());
        existing.setPrice(serviceOffering.getPrice());
        existing.setVisible(serviceOffering.isVisible());

        serviceOfferingRepository.save(existing);
        return new Message("Servicio actualizado con exito", true);
    }

    public Message toggleVisibility(int serviceId) {
        ServiceOffering existing = serviceOfferingRepository.findById(serviceId)
                .orElseThrow(() -> new IllegalArgumentException("El servicio no existe"));
        existing.setVisible(!existing.isVisible());
        serviceOfferingRepository.save(existing);
        return new Message("Visibilidad actualizada con exito", true);
    }
}

