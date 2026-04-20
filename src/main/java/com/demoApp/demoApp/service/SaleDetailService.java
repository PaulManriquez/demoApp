package com.demoApp.demoApp.service;

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

    //Save a saleDetail based on a sale object
    public Message saveSaleDetail(Sale sale){

        SaleDetail saleDetail = new SaleDetail();

        // Sale
        saleDetail.setSale(sale);
        // Product
        saleDetail.setProduct(saleDetail.getProduct());
        // Quantity
        saleDetail.setQuantity(saleDetail.getQuantity());
        // Price
        saleDetail.setPrice(BigDecimal.valueOf(saleDetail.getProduct().getRetailPrice()));
        // Subtotal
        BigDecimal subtotal = BigDecimal.valueOf(saleDetail.getQuantity() * saleDetail.getProduct().getRetailPrice());
        saleDetail.setSubtotal(subtotal);

        // Save sale detail
        saleDetailRepository.save(saleDetail);

        return new Message("Venta concretada",true);
    }

    // Return the total according to all the saleIds related in a saleDetailsList
    // getting at the end "the total of all salesIds related"
    public BigDecimal getTotalBySaleId(Integer saleId){

        // Find all the sales_details related to a sale
        List<SaleDetail> saleDetailsList =  saleDetailRepository.findBySaleId(saleId);

        // Initialize variables
        BigDecimal total = BigDecimal.ZERO;

        // Calculate the total based on the subtotals
        for(SaleDetail saleDetail: saleDetailsList){
            total.add(saleDetail.getSubtotal());
        }

        return total;
    }
}
