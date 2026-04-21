package com.demoApp.demoApp.service;

import com.demoApp.demoApp.entity.Product;
import com.demoApp.demoApp.entity.User;
import com.demoApp.demoApp.model.Message;
import com.demoApp.demoApp.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

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

    public Product getProductById(Integer productId){
        Optional<Product> optProduct = productRepository.findById(productId);

        if(optProduct.isPresent())
            return optProduct.get();

        return null;
    }

    public Message saveCreateProduct(Product product) {
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

    public Message updateProduct(Product product) {

        // Verify product do not exist already
        if(product.getId() == null){
            return new Message("El producto no existe, no es valido", false);
        }

        //Update the product fields
        try{
            Product productToUpdate = productRepository.findById(product.getId()).get();
            updateEditableProductFields(productToUpdate,product);

            productRepository.save(productToUpdate);
            return new Message("Producto actualizado con ėxito", true);
        }catch (IllegalArgumentException ex){
            return new Message("No se encontro el producto a actualizar", false);
        }
    }

    private void updateEditableProductFields(Product productToUpdate, Product product){
        productToUpdate.setName(product.getName());
        productToUpdate.setItems(product.getItems());
        productToUpdate.setWholesalePrice(product.getWholesalePrice());
        productToUpdate.setRetailPrice(product.getRetailPrice());
        productToUpdate.setSpecial(product.isSpecial());
    }

}
