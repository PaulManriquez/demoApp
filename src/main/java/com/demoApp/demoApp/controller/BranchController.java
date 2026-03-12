package com.demoApp.demoApp.controller;

import org.springframework.ui.Model;
import com.demoApp.demoApp.entity.Branch;
import com.demoApp.demoApp.service.BranchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/branches")
public class BranchController {

    private static final Logger logger =
            LoggerFactory.getLogger(BranchController.class);

    @Autowired
    private BranchService branchService;

    @GetMapping("/")
    public String showAdminHome(Model model){
        model.addAttribute("branches", branchService.getAllBranches()); //Used to convey each branch object to the front
        model.addAttribute("branch",new Branch()); // FORM BINDING: Used as form tag object to point to a branch object

        return "administration/branches/index";
    }

    @ModelAttribute
    public void setGenerics(Model model){
        model.addAttribute("position", "branches");
    }
}
