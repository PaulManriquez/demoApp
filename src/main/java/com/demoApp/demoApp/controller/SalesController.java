package com.demoApp.demoApp.controller;

import com.demoApp.demoApp.entity.Sale;
import com.demoApp.demoApp.model.Message;
import com.demoApp.demoApp.service.ClientsService;
import com.demoApp.demoApp.service.ProductService;
import com.demoApp.demoApp.service.SaleDetailService;
import com.demoApp.demoApp.service.SalesService;
import com.demoApp.demoApp.service.StockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/sales")
public class SalesController {

    private static final Logger logger = LoggerFactory.getLogger(SalesController.class);

    private final SalesService saleService;

    private final ClientsService clientService;

    private final ProductService productService;

    private final StockService stockService;

    private final SaleDetailService saleDetailService;

    @Autowired
    public SalesController(SalesService salesService,ClientsService clientService,
                           ProductService productService, StockService stockService,
                           SaleDetailService saleDetailService){
        this.saleService = salesService;
        this.clientService = clientService;
        this.productService = productService;
        this.stockService = stockService;
        this.saleDetailService = saleDetailService;
    }

    @GetMapping({"","/"})
    public String showSalesPage(Model model){
        model.addAttribute("sale", new Sale());
        model.addAttribute("sales", saleService.getAllSales());
        model.addAttribute("clients",clientService.getAllClients());

        return "administration/sales/index";
    }

    // | Guardar | btn
    @PostMapping({"", "/"})
    public String saveSale(Sale sale, RedirectAttributes attributes) {

        Message message = saleService.saveCreateSale(sale);

        attributes.addFlashAttribute("msg", message);

        return "redirect:/sales/products/" + sale.getId();
    }

    @GetMapping("/products/{saleId}")
    public String showSaleProducts(@PathVariable Integer saleId, Model model){

        // Get sale by sale id
        Optional<Sale> optSale = saleService.getSaleById(saleId);

        // *** To be updated and handled
        if(!optSale.isPresent())
            return "error";

        // Sale object model
        model.addAttribute("sale",optSale.get());

        // All products (for dropdown display-select)
        model.addAttribute("products",productService.getAllProducts());

        // To display all the available stock (Not sold yet)
        model.addAttribute("availableStock", stockService.getAvailableStock());

        // Total
        model.addAttribute("total",saleDetailService.getTotalBySaleId(saleId));

        return "administration/sales/products";
    }

//    @PostMapping("/products/add")
//    public String addProductToSale(@RequestParam Integer quantity,
//                                   @RequestParam BigDecimal price,
//                                   Sale sale,
//                                   Product product) {
//
//        // 1. Get available stock
//        List<Stock> available = stockService
//                .findAvailableByProduct(product.getId(), quantity);
//
//        if (available.size() < quantity) {
//            throw new RuntimeException("Not enough stock");
//        }
//
//        // 2. Assign stock to sale
//        for (Stock s : available) {
//            s.setSale(sale);
//            stockService.save(s);
//        }
//
//        // 3. Create sale_detail
//        SaleDetail detail = new SaleDetail();
//
//        detail.setSale(sale);
//        detail.setProduct(product);
//        detail.setQuantity(quantity);
//        detail.setPrice(price);
//        detail.setSubtotal(price.multiply(BigDecimal.valueOf(quantity)));
//
//        saleDetailService.save(detail);
//
//        return "redirect:/sales/products/" + sale.getId();
//    }

    @ModelAttribute
    public void setGenerics(Model model) {
        model.addAttribute("position", "sales");
    }

}
