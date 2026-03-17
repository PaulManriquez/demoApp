package com.demoApp.demoApp.controller;

import com.demoApp.demoApp.Model.Message;
import com.demoApp.demoApp.entity.User;
import com.demoApp.demoApp.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import com.demoApp.demoApp.entity.Branch;
import com.demoApp.demoApp.service.BranchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/branches")
public class BranchController {

    private static final Logger logger =
            LoggerFactory.getLogger(BranchController.class);

    @Autowired
    private BranchService branchService;

    @Autowired
    private UserService userService;

    @GetMapping({"", "/"})
    public String showAdminHome(Model model){
        model.addAttribute("branches", branchService.getAllBranches()); //Used to convey each branch object to the front
        model.addAttribute("branch",new Branch()); // FORM BINDING: Used as form tag object to point to a branch object

        return "administration/branches/index";
    }

    @PostMapping({"", "/"})
    public String saveBranch(Branch branch, BindingResult result, RedirectAttributes attributes){

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
        branchService.save(branch);
        //
        attributes.addFlashAttribute("msg",new Message("Sucursal guardada con exito",true));
        return "redirect:/branches/";
    }

    @ModelAttribute
    public void setGenerics(Model model){
        model.addAttribute("position", "branches");
    }
}
