package com.demoApp.demoApp.controller;

import com.demoApp.demoApp.entity.Product;
import com.demoApp.demoApp.model.Message;
import com.demoApp.demoApp.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
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
    private Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductService productService;

    @GetMapping({"", "/"})
    public String showAdminHome(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "administration/products/index";
    }

    @PostMapping({"", "/"})
    public String saveProduct(@ModelAttribute Product product, BindingResult result, RedirectAttributes attributes, Model model) {

        // Validate no errors on create a product, assigning the authenticated user as owner.
        if (hasValidationErrors(result)) {
            model.addAttribute("products", productService.getAllProducts());
            return "administration/products/index";
        }

        // Get the authenticated user. This service now expects a valid authenticated user or an exception.
        // Save the product
        Message message = productService.save(product);
        attributes.addFlashAttribute("msg", message);

        return "redirect:/products/";
    }

    @PutMapping("/update-product")
    public String updateProduct(Product product, BindingResult result, RedirectAttributes attributes, Model model){

        if (hasValidationErrors(result)) {
            model.addAttribute("products", productService.getAllProducts());
            return "administration/products/index";
        }

        Message message = productService.updateProduct(product);
        attributes.addFlashAttribute("msg", message);
        return "redirect:/products/";
    }

//    @PutMapping("/")
//    public String updateProduct(Product product, BindingResult result, RedirectAttributes attributes){
//        if(result.hasErrors()){
//            for (ObjectError error: result.getAllErrors()) {
//                logger.warn("Error: {}", error.getDefaultMessage());
//            }
//            return "administration/products/index";
//        }
//        Message message = productService.updateProduct(product);
//
//        attributes.addFlashAttribute("msg", message);
//        return "redirect:/products/";
//    }

    @ModelAttribute
    public void setGenerics(Model model){model.addAttribute("position", "products");}

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