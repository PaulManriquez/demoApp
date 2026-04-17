package com.demoApp.demoApp.controller;

import com.demoApp.demoApp.entity.Purchase;
import com.demoApp.demoApp.entity.Stock;
import com.demoApp.demoApp.model.Message;
import com.demoApp.demoApp.service.ProductService;
import com.demoApp.demoApp.service.ProviderService;
import com.demoApp.demoApp.service.PurchaseService;
import com.demoApp.demoApp.service.StockService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/purchases")
public class PurchaseController {

    private static final Logger logger =
            LoggerFactory.getLogger(PurchaseController.class);

    private final PurchaseService purchaseService;

    private final ProviderService providerService;
    private final ProductService productService;

    private final StockService stockService;

    @Autowired
    public PurchaseController(PurchaseService purchaseService, ProviderService providerService,
                              ProductService productService, StockService stockService) {
        this.purchaseService = purchaseService;
        this.providerService = providerService;
        this.productService = productService;
        this.stockService = stockService;
    }

    @GetMapping({"", "/"})
    public String showPurchasesMainPage(Model model) {
        return reloadPurchasesPage(model, new Purchase());
    }

    @PostMapping("/")
    public String savePurchase(@Valid Purchase purchase, BindingResult result, RedirectAttributes attributes, Model model) {


        logger.info(
                "savePurchase() received date={}, providerId={}",
                purchase.getDate(),
                purchase.getProvider() != null ? purchase.getProvider().getId() : null
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

    // | Edit button |
    @PutMapping("/update")
    public String updatePurchase(@Valid Purchase purchase, BindingResult result,
                                 RedirectAttributes attributes, Model model){

        logger.info(
                "updatePurchase() received date={}, providerId={}",
                purchase.getDate(),
                purchase.getProvider() != null ? purchase.getProvider().getId() : null
        );

        // Reload the form with validation errors
        if (hasValidationErrors(result)) {
            return reloadPurchasesPage(model, purchase);
        }

        // Update purchase
        Message message = purchaseService.updatePurchase(purchase);

        // Display message result after updating a new purchase
        attributes.addFlashAttribute("msg", message);

        return "redirect:/purchases/";
    }

    // “Now manage the ITEMS of this purchase” | The products that belong to the purchase
    // | Productos | button
    @GetMapping("/products/{id}")
    public String showEditPurchaseProducts(@PathVariable Integer id, Model model) {

        // Providers
        // Purchase ID [ lo know to witch purchase will be linked the products]
        model.addAttribute("purchase",purchaseService.getPurchaseById(id));

        // Products
        // Load all the products [ lo be able to link it to the purchase]
        model.addAttribute("products",productService.getAllProducts());
        // stockItems related to this purchase
        model.addAttribute("stockItems", stockService.getStockByPurchaseId(id));

        // Total stock price by customer
        model.addAttribute("totalPurchase",stockService.getTotalSalePriceByPurchaseId(id));

        // Page
        return "administration/purchases/products";
    }

    @PostMapping("/products/add")
    public String addProductToPurchase(Stock stock ,RedirectAttributes attributes,
                                       @RequestParam Integer quantity){

        Message message = stockService.addProductsToPurchase(stock, quantity);

        attributes.addFlashAttribute("msg", message);

        return "redirect:/purchases/products/" + stock.getPurchase().getId(); // Building a dynamic url
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
