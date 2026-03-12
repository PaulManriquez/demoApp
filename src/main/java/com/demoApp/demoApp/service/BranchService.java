package com.demoApp.demoApp.service;

import com.demoApp.demoApp.entity.Branch;
import com.demoApp.demoApp.repository.BranchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BranchService {

    @Autowired
    private BranchRepository branchRepository;

    public List<Branch> getAllBranches(){
        return branchRepository.findAll();
    }

}
