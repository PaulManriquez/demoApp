package com.demoApp.demoApp.service;

import com.demoApp.demoApp.entity.Branch;
import com.demoApp.demoApp.entity.Product;
import com.demoApp.demoApp.entity.User;
import com.demoApp.demoApp.model.Message;
import com.demoApp.demoApp.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class ProductService {

    private ProductRepository productRepository;

    private UserService userService;

    @Autowired
    public ProductService(ProductRepository productRepository, UserService userService) {
        this.userService = userService;
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts(){
        return productRepository.findAll();
    }

    public Message save(Product product) {
        if (product.getId() != null){
            return new Message("El producto ya existe", false);
        }

        // Get the authenticated user. This service now expects a valid authenticated user or an exception.
        User user = userService.getCurrentlyAuthenticatedUser();

        // Initialize creation-only fields for a new product.
        product.setCreatedAt(Instant.now());
        product.setActive(true);
        product.setUser(user);

        // Save the product
        productRepository.save(product);
        return new Message("Producto guardado con exito", true);
    }

//    public Message saveCreateBranch(Branch branch){
//
//        if (branch.getId() != null) {
//            return new Message("La sucursal ya existe", false);
//        }
//
//        // Get the authenticated user. This service now expects a valid authenticated user or an exception.
//        User user = userService.getCurrentlyAuthenticatedUser();
//
//        // Initialize creation-only fields for a new branch.
//        branch.setCreatedAt(Instant.now());
//        branch.setActive(true);
//        branch.setUser(user);
//
//        // Save the branch
//        branchRepository.save(branch);
//        return new Message("Sucursal guardada con exito", true);
//    }


//
//    public Message save(Product product) {
//        if (product.getId()== null){
//            product.setCreatedAt(Instant.now());
//            product.setActive(true);
//        }
//
//        productRepository.save(product);
//        return new Message("Producto guardado con exito", true);
//    }
//
//    public Message updateProduct(Product product) {
//        Optional<Product> optionalProduct = productRepository.findById(product.getId());
//
//        if (optionalProduct.isPresent()) {
//            Product productToBeUpdated = optionalProduct.get();
//            productToBeUpdated.setName(product.getName());
//            productToBeUpdated.setItems(product.getItems());
//            productToBeUpdated.setWholesalePrice(product.getWholesalePrice());
//            productToBeUpdated.setRetailPrice(product.getRetailPrice());
//            productToBeUpdated.setSpecial(product.isSpecial());
//            productRepository.save(productToBeUpdated);
//            return new Message("Producto actualizado con ėxito", true);
//        } else {
//            return new Message("Hay un problema al actualizar el producto.", false);
//        }
//    }

}
