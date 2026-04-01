package com.demoApp.demoApp.service;

import com.demoApp.demoApp.Model.Message;
import com.demoApp.demoApp.entity.Branch;
import com.demoApp.demoApp.entity.User;
import com.demoApp.demoApp.repository.BranchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

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

    public Message save(Branch branch){

        // Get the authenticated user | findUserByUsernameOrEmail already throws an exception that's
        // why no need to check if user is null
        User user = userService.getCurrentlyAuthenticatedUser();

        // Set the owner of the branch
        if(branch.getId()==null){ // Why check if branch is null?
            branch.setCreatedAt(Instant.now());
            branch.setActive(true);
        }

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

        Optional<Branch> optBranch = branchRepository.findById(branch.getId());
        if (optBranch.isPresent()) {
            Branch branchToUpdate = optBranch.get();
            //Note: id already comes in the branch object ,  so here is not being set/updated
            branchToUpdate.setName(branch.getName());
            branchToUpdate.setAddress(branch.getAddress());
            branchToUpdate.setMapsLink(branch.getMapsLink());

            branchRepository.save(branchToUpdate);//Automatically knows by the id that is referring to this branch
            return new Message("Sucursal actualizada con exito", true);
        }

        return new Message("No se encontro la sucursal a actualizar", false);
    }

    public Message toggleBranchStatusVisibleOnOff(int branchId){
        Optional<Branch> optBranch = branchRepository.findById(branchId);

        if (optBranch.isPresent()) {
            Branch branchToUpdate = optBranch.get();
            branchToUpdate.setActive(!branchToUpdate.isActive()); // Toggle active

            branchRepository.save(branchToUpdate);
            return new Message("Estado de la sucursal actualizado con exito", true);
        }

        return new Message("No se pudo actualizar el estado de la sucursal", false);
    }

}
