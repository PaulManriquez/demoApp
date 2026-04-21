package com.demoApp.demoApp.service;

import com.demoApp.demoApp.entity.Product;
import com.demoApp.demoApp.entity.Sale;
import com.demoApp.demoApp.entity.Stock;
import com.demoApp.demoApp.repository.SaleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.demoApp.demoApp.model.Message;
import com.demoApp.demoApp.entity.User;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class SalesService {

    private static final Logger logger = LoggerFactory.getLogger(SalesService.class);

    private final SaleRepository saleRepository;

    private final UserService userService;

    private final StockService stockService;

    @Autowired
    public SalesService(SaleRepository saleRepository, UserService userService, StockService stockService){
        this.saleRepository = saleRepository;
        this.userService = userService;
        this.stockService = stockService;
    }

    public List<Sale> getAllSales(){
        return saleRepository.findAll();
    }

    public Sale getSaleById(Integer saleId){

        Optional<Sale> optSale = saleRepository.findById(saleId);

        if(optSale.isPresent())
            return optSale.get();

        return null;
    }

    public Message saveCreateSale(Sale sale){

        // Verify purchase do not exist already
        if(sale.getId() != null)
            return new Message("La venta ya existe", false);

        // Ensure that is a valid user authenticated
        User user = userService.getCurrentlyAuthenticatedUser(); // NOTE: here should i retrieve a client user?
        logger.info("The user {} made a sale. | SalesService save create sale",user.getUsername());

        sale.setDate(Instant.now());
        sale.setCreatedAt(Instant.now());
        sale.setUser(user);
        //sale.setClient(); // Currently comes from the form, in the data binder

        saleRepository.save(sale);

        return new Message("Venta guardada con exito", true);
    }

    public Message setSaleToListStock(List<Stock> availableStock,Sale sale){

        // Stablish every sale to the stock
        for(Stock s: availableStock){
            s.setSale(sale);
            stockService.saveProductStock(s);
        }
        return new Message("La venta se realizo y se actualizo el stock disponible",true);
    }

}
