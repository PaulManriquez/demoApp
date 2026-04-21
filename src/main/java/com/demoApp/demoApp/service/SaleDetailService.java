package com.demoApp.demoApp.service;

import com.demoApp.demoApp.entity.Product;
import com.demoApp.demoApp.entity.Sale;
import com.demoApp.demoApp.entity.SaleDetail;
import com.demoApp.demoApp.model.Message;
import com.demoApp.demoApp.repository.SaleDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class SaleDetailService {

    private final SaleDetailRepository saleDetailRepository;

    @Autowired
    public SaleDetailService(SaleDetailRepository saleDetailRepository){
        this.saleDetailRepository = saleDetailRepository;
    }

    public Message saveSaleDetail(Sale sale, Product product, Integer quantity, BigDecimal price){

        SaleDetail detail = new SaleDetail();

        detail.setSale(sale);
        detail.setProduct(product);
        detail.setQuantity(quantity);
        detail.setPrice(price);
        detail.setSubtotal(price.multiply(BigDecimal.valueOf(quantity)));

        saleDetailRepository.save(detail);

        return new Message("Venta concretada",true);
    }

    // Return the total according to all the saleIds related in a saleDetailsList
    // getting at the end "the total of all salesIds related"
    public BigDecimal getTotalBySaleId(Integer saleId){

        // Find all the sales_details related to a sale
        List<SaleDetail> saleDetailsList =  saleDetailRepository.getAllSaleDetailsById(saleId);

        // Initialize variables
        BigDecimal total = BigDecimal.ZERO;

        // Calculate the total based on the subtotals
        for(SaleDetail saleDetail: saleDetailsList){
            total = total.add(saleDetail.getSubtotal());
        }

        return total;
    }

    public List<SaleDetail> getAllSaleDetailsById(Integer saleId){
        return saleDetailRepository.getAllSaleDetailsById(saleId);
    }

}
