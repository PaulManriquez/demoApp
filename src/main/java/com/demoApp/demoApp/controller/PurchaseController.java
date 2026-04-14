package com.demoApp.demoApp.controller;

import com.demoApp.demoApp.entity.Purchase;
import com.demoApp.demoApp.model.Message;
import com.demoApp.demoApp.service.ProviderService;
import com.demoApp.demoApp.service.PurchaseService;
import com.demoApp.demoApp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.ui.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/purchases")
public class PurchaseController {

    private static final Logger logger =
            LoggerFactory.getLogger(PurchaseController.class);

    private final PurchaseService purchaseService;

    private final ProviderService providerService;

    private final UserService userService;

    public PurchaseController(PurchaseService purchaseService, ProviderService providerService, UserService userService) {
        this.purchaseService = purchaseService;
        this.providerService = providerService;
        this.userService = userService;
    }

    @GetMapping({"", "/"})
    public String showPurchasesMainPage(Model model) {
        return reloadPurchasesPage(model, new Purchase());
    }

    @PostMapping("/")
    public String savePurchase(@Valid Purchase purchase, BindingResult result, RedirectAttributes attributes, Model model) {


        logger.info(
                "updatePurchase() received date={}, providerId={}, userId={}",
                purchase.getDate(),
                purchase.getProvider() != null ? purchase.getProvider().getId() : null,
                userService.getCurrentlyAuthenticatedUser()
        );

        // Validate Errors
        if (hasValidationErrors(result)) {
            return reloadPurchasesPage(model, purchase);
        }

        // Create purchase
        Message message = purchaseService.saveCreatePurchase(purchase);

        // Display message result after creating a new purchase
        attributes.addFlashAttribute("msg", message);

        return "redirect:/purchases/";
    }

    @PutMapping("/update")
    public String updatePurchase(Purchase purchase, BindingResult result, RedirectAttributes attributes){

        logger.info(
                "updatePurchase() received date={}, providerId={}, userId={}",
                purchase.getDate(),
                purchase.getProvider() != null ? purchase.getProvider().getId() : null,
                userService.getCurrentlyAuthenticatedUser()
        );

        // Re direct to main page
        if(hasValidationErrors(result)){return "redirect:/purchases/";}

        // Update purchase
        Message message = purchaseService.updatePurchase(purchase);

        // Display message result after updating a new purchase
        attributes.addFlashAttribute("msg", message);

        return "redirect:/purchases/";
    }

    @ModelAttribute
    public void setGenerics(Model model) {
        model.addAttribute("position", "purchase");
    }

    private String reloadPurchasesPage(Model model, Purchase purchase) {
        model.addAttribute("purchase", purchase);
        model.addAttribute("purchases", purchaseService.getAllPurchases());
        model.addAttribute("providers", providerService.getAllProviders());
        return "administration/purchases/index";
    }

    private boolean hasValidationErrors(BindingResult result) {
        if (!result.hasErrors()) {
            return false;
        }

        for (ObjectError error : result.getAllErrors()) {
            logger.warn("Error: {}", error.getDefaultMessage());
        }

        return true;
    }

}
