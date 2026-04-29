package com.demoApp.demoApp.service;

import com.demoApp.demoApp.entity.Client;
import com.demoApp.demoApp.entity.User;
import com.demoApp.demoApp.model.Message;
import com.demoApp.demoApp.repository.ClientsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientService {

    private final ClientsRepository clientsRepository;
    private final UserService userService;

    @Autowired
    public ClientService(ClientsRepository clientsRepository, UserService userService) {
        this.clientsRepository = clientsRepository;
        this.userService = userService;
    }

    public List<Client> getAllClients() {
        return clientsRepository.findAll();
    }

    public Message createClient(Client client) {
        if (client.getId() != null) {
            return new Message("El cliente ya existe", false);
        }

        User user = userService.getCurrentlyAuthenticatedUser();
        client.setUser(user);
        client.setActive(true);
        clientsRepository.save(client);
        return new Message("Cliente guardado con exito", true);
    }

    public Message updateClient(Client client) {
        if (client.getId() == null) {
            return new Message("El cliente no existe", false);
        }

        Client existing = clientsRepository.findById(client.getId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente no existe"));

        existing.setFirstName(client.getFirstName());
        existing.setLastName(client.getLastName());
        existing.setAddress(client.getAddress());
        existing.setMapsLink(client.getMapsLink());
        existing.setPhone(client.getPhone());

        clientsRepository.save(existing);
        return new Message("Cliente actualizado con exito", true);
    }

    public Message toggleActive(int clientId) {
        Client existing = clientsRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no existe"));
        existing.setActive(!existing.getActive());
        clientsRepository.save(existing);
        return new Message(existing.getActive() ? "Cliente habilitado" : "Cliente deshabilitado", true);
    }
}
