package com.demoApp.demoApp.service;

import com.demoApp.demoApp.model.Message;
import com.demoApp.demoApp.entity.Branch;
import com.demoApp.demoApp.entity.User;
import com.demoApp.demoApp.repository.BranchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class BranchService {

    private final BranchRepository branchRepository;

    private final UserService userService;

    @Autowired
    public BranchService(BranchRepository branchRepository, UserService userService) {
        this.branchRepository = branchRepository;
        this.userService = userService;
    }

    public List<Branch> getAllBranches(){
        return branchRepository.findAll();
    }

    public Message saveCreateBranch(Branch branch){

        if (branch.getId() != null) {
            return new Message("La sucursal ya existe", false);
        }

        // Get the authenticated user. This service now expects a valid authenticated user or an exception.
        User user = userService.getCurrentlyAuthenticatedUser();

        // Initialize creation-only fields for a new branch.
        branch.setCreatedAt(Instant.now());
        branch.setActive(true);
        branch.setUser(user);

        // Save the branch
        branchRepository.save(branch);
        return new Message("Sucursal guardada con exito", true);
    }

    public Message updateBranch(Branch branch){
        //Ensure id branch exist
        if (branch.getId() == null) {
            return new Message("La sucursal no existe", false);
        }

        try {
            Branch branchToUpdate = getBranchById(branch.getId());
            updateEditableBranchFields(branchToUpdate, branch);

            branchRepository.save(branchToUpdate);//Automatically knows by the id that is referring to this branch
            return new Message("Sucursal actualizada con exito", true);
        } catch (IllegalArgumentException ex) {
            return new Message("No se encontro la sucursal a actualizar", false);
        }
    }

    public Message toggleBranchActiveStatus(int branchId){
        try {
            Branch branchToUpdate = getBranchById(branchId);
            branchToUpdate.setActive(!branchToUpdate.isActive()); // Toggle active

            branchRepository.save(branchToUpdate);
            return new Message("Estado de la sucursal actualizado con exito", true);
        } catch (IllegalArgumentException ex) {
            return new Message("No se pudo actualizar el estado de la sucursal", false);
        }
    }

    private Branch getBranchById(Integer branchId) {
        return branchRepository.findById(branchId)
                .orElseThrow(() -> new IllegalArgumentException("La sucursal no existe"));
    }

    private void updateEditableBranchFields(Branch targetBranch, Branch sourceBranch) {
        targetBranch.setName(sourceBranch.getName());
        targetBranch.setAddress(sourceBranch.getAddress());
        targetBranch.setMapsLink(sourceBranch.getMapsLink());
    }

}
