package com.demoApp.demoApp.service;

import com.demoApp.demoApp.entity.Stock;
import com.demoApp.demoApp.model.Message;
import com.demoApp.demoApp.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Transactional
    public Message addProductsToPurchase(Stock stock, Integer quantity) {
        for (int i = 0; i < quantity; i++) {
            Stock newStock = new Stock();
            newStock.setPurchase(stock.getPurchase());
            newStock.setProduct(stock.getProduct());
            newStock.setDescription(stock.getDescription());
            newStock.setPurchasePrice(stock.getPurchasePrice());
            newStock.setSalePrice(stock.getSalePrice());
            stockRepository.save(newStock);
        }

        return new Message("Producto guardado en esta compra", true);
    }

    public BigDecimal getTotalSalePriceByPurchaseId(Integer purchaseId) {
        return stockRepository.sumSalePriceByPurchaseId(purchaseId);
    }

    public List<Stock> getAvailableStockProductByProductId(Integer productId, Integer quantity){
        return stockRepository.findAvailableStockByProductId(productId, quantity);
    }

    public List<Stock> getAvailableStock(){
        return stockRepository.getAllAvailableStock();
    }

    public Map<Integer, Long> getAvailableStockByProductIdAnQuantityGroup(){

        // Query results
        List<Object[]> results = stockRepository.getAvailableStockByProductIdAndQuantity();

        // Map to save the results
        Map<Integer, Long> map = new HashMap<>();

        // Process the results
        for(Object[] row: results){
            Integer productId = (Integer) row[0]; // Product Id: to know who product is referring to
            Long count = (Long) row[1]; // Set the numbers of the query
            map.put(productId, count);
        }

        return map;
    }

}
