package com.demoApp.demoApp.service;

import com.demoApp.demoApp.entity.Client;
import com.demoApp.demoApp.repository.ClientsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientsService {

    private final ClientsRepository clientsRepository;

    @Autowired
    public ClientsService(ClientsRepository clientsRepository){
        this.clientsRepository = clientsRepository;
    }

    public List<Client> getAllClients(){
        return clientsRepository.findAll();
    }

}
