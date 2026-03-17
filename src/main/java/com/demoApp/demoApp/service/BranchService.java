package com.demoApp.demoApp.service;

import com.demoApp.demoApp.Model.Message;
import com.demoApp.demoApp.entity.Branch;
import com.demoApp.demoApp.repository.BranchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

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

}
