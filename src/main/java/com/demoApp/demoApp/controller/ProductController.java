package com.demoApp.demoApp.controller;

import com.demoApp.demoApp.entity.Product;
import com.demoApp.demoApp.model.Message;
import com.demoApp.demoApp.service.ProductService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/products")
public class ProductController {
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping({"", "/"})
    public String showProductsPage(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("product", new Product());
        return "administration/products/index";
    }

    @PostMapping({"", "/"})
    public String saveProduct(@Valid @ModelAttribute Product product, BindingResult result, RedirectAttributes attributes, Model model) {

        logger.info("In saveProduct() | {}", ProductController.class);

        if (hasValidationErrors(result)) {
            return reloadProductsPage(model);
        }

        // Persist the new product and expose the result as a flash message.
        Message message = productService.saveCreateProduct(product);
        attributes.addFlashAttribute("msg", message);

        return "redirect:/products/";
    }

    @PutMapping("/update-product")
    public String updateProduct(@Valid Product product, BindingResult result, RedirectAttributes attributes, Model model){

        logger.info("In updateProduct() | {}", ProductController.class);

        if (hasValidationErrors(result)) {
            return reloadProductsPage(model);
        }

        Message message = productService.updateProduct(product);
        attributes.addFlashAttribute("msg", message);
        return "redirect:/products/";
    }

    @ModelAttribute
    public void setGenerics(Model model) {
        model.addAttribute("position", "products");
    }

    private String reloadProductsPage(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("product", new Product());
        return "administration/products/index";
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
