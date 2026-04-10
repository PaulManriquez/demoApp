package com.demoApp.demoApp.controller;

import com.demoApp.demoApp.entity.Purchase;
import com.demoApp.demoApp.model.Message;
import com.demoApp.demoApp.service.ProviderService;
import com.demoApp.demoApp.service.PurchaseService;
import org.springframework.ui.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/purchases")
public class PurchaseController {

    private static final Logger logger =
            LoggerFactory.getLogger(PurchaseController.class);


    private final PurchaseService purchaseService;

    private final ProviderService providerService;

    public PurchaseController(PurchaseService purchaseService, ProviderService providerService) {
        this.purchaseService = purchaseService;
        this.providerService = providerService;
    }

    @GetMapping({"", "/"})
    public String showAdminHome(Model model) {
        model.addAttribute("purchase", new Purchase());
        model.addAttribute("purchases", purchaseService.getAllPurchases());
        model.addAttribute("providers", providerService.getAllProviders());
        return "administration/purchases/index";
    }

    @PostMapping("/")
    public String savePurchase(Purchase purchase, BindingResult result, RedirectAttributes attributes, Model model) {

        logger.info(
                "savePurchase() received date={}, providerId={}, userId={}",
                purchase.getDate(),
                purchase.getProvider() != null ? purchase.getProvider().getId() : null,
                purchase.getUser() != null ? purchase.getUser().getId() : null
        );

        if (hasValidationErrors(result)) {
            return "administration/purchases/index";
        }

        Message message = purchaseService.saveCreatePurchase(purchase);
        attributes.addFlashAttribute("msg", message);

        return "redirect:/purchases/";
    }

    @ModelAttribute
    public void setGenerics(Model model) {
        model.addAttribute("position", "purchase");
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
