package com.demoApp.demoApp.service;

import com.demoApp.demoApp.Model.Message;
import com.demoApp.demoApp.entity.Branch;
import com.demoApp.demoApp.repository.BranchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class BranchService {

    @Autowired
    private BranchRepository branchRepository;

    public List<Branch> getAllBranches(){
        return branchRepository.findAll();
    }

    public Message save(Branch branch){
        if(branch.getId()==null){ // Why check if branch is null?
            branch.setCreatedAt(Instant.now());
            branch.setActive(true);
        }

        branchRepository.save(branch);
        return new Message("Sucursal guardada con exito",true);
    }

    public Message updateBranch(Branch branch){
        //Ensure id branch exist
        if(branch.getId() == null){
            return new Message("Branch Sucursal error updating branch doesnt exist", false);
        }

        Optional<Branch> optBranch = branchRepository.findById(branch.getId());
        if(optBranch.isPresent()){
            Branch branchToUpdate =  optBranch.get();
            //Note: id already comes in the branch object ,  so here is not being set/updated
            branchToUpdate.setName(branch.getName());
            branchToUpdate.setAddress(branch.getAddress());
            branchToUpdate.setMapsLink(branch.getMapsLink());

            branchRepository.save(branchToUpdate);//Automatically knows by the id that is referring to this branch
            return new Message("Branch Sucursal updated correctly",true);
        }

        return new Message("Branch Sucursal failed unexpectedly",true);
    }

}
