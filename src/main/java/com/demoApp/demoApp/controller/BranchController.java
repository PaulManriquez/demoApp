package com.demoApp.demoApp.controller;

import com.demoApp.demoApp.Model.Message;
import com.demoApp.demoApp.entity.User;
import com.demoApp.demoApp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import com.demoApp.demoApp.entity.Branch;
import com.demoApp.demoApp.service.BranchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/branches")
public class BranchController {

    private static final Logger logger =
            LoggerFactory.getLogger(BranchController.class);

    private final BranchService branchService;

    private final UserService userService;

    @Autowired
    public BranchController(BranchService branchService, UserService userService) {
        this.branchService = branchService;
        this.userService = userService;
    }

    // Load the branches admin page with the current branch list and an empty form model.
    @GetMapping({"", "/"})
    public String showAdminHome(Model model){
        model.addAttribute("branches", branchService.getAllBranches()); //Used to convey each branch object to the front
        model.addAttribute("branch",new Branch()); // FORM BINDING: Used as form tag object to point to a branch object

        return "administration/branches/index";
    }

    // Validate and create a new branch, assigning the authenticated user as owner.
    @PostMapping({"", "/"})
    public String saveBranch(@Valid Branch branch, BindingResult result, RedirectAttributes attributes){

        if(result.hasErrors()){
            for(ObjectError error: result.getAllErrors()){
                logger.warn("Error: {}",error.getDefaultMessage());
            }
            return "administration/branches/index";
        }

        Authentication  auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(auth.getName());

        logger.info("=== {} === {}",user.getUsername(),user.getRoles());

        branch.setUser(user);

        //Save new branch
        Message message = branchService.save(branch);
        //
        attributes.addFlashAttribute("msg",new Message(message.getBody(), message.isSuccess()));
        return "redirect:/branches/";
    }

    // Validate and persist edits to an existing branch, then redirect back to the list.
    @PutMapping("/update-branch")
    public String updateBranch(@Valid Branch branch, BindingResult result, RedirectAttributes attributes){

        logger.info("In uptateBranch() | {}",BranchController.class);

        if(result.hasErrors()){
            for(ObjectError error: result.getAllErrors()){
                logger.warn("Error: {}",error.getDefaultMessage());
            }
            return "administration/branches/index";
        }

        Message message = branchService.updateBranch(branch);
        attributes.addFlashAttribute("msg",new Message(message.getBody(), message.isSuccess()));

        return "redirect:/branches/";
    }

    // Toggle the status of a branch identified by its id and return to the list view.
    @GetMapping("/status/{id}")
    public String deleteBranch(@PathVariable("id") int branchId, RedirectAttributes attributes){

        logger.info("In deleteBranch() | {}",BranchController.class);

        Message message = branchService.updateStatus(branchId);
        attributes.addFlashAttribute("The status has been updated", message);
        return "redirect:/branches/";
    }

    // Add shared view metadata used to highlight the active administration section.
    @ModelAttribute
    public void setGenerics(Model model){
        model.addAttribute("position", "branches");
    }
}
