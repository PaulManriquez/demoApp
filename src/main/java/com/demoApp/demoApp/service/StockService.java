package com.demoApp.demoApp.service;

import com.demoApp.demoApp.entity.Stock;
import com.demoApp.demoApp.model.Message;
import com.demoApp.demoApp.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockService {

    private final StockRepository stockRepository;

    @Autowired
    public StockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    public List<Stock> getStockByPurchaseId(Integer purchaseId) {
        return stockRepository.findByPurchaseId(purchaseId); // How much stock do we have by this purchase
    }

    public Message saveProductStock(Stock stock){
        stockRepository.save(stock);
        return new Message("Producto guardado en esta compra",true);
    }

}
