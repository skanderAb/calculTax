package com.adventium.taxcompute.services;

import com.adventium.taxcompute.model.Product;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ProductServiceImpl implements ProductService {

    private static final Double IMPORT_TAXES = 0.05;

    public ProductServiceImpl() {
    }

    private Double round5Tax(Double tax) {
        return Math.round(tax * 20) / 20.0;
    }

    private Double computeProductTTCPrice(Product product, Double tax) {
        return round5Tax(product.getQuantity() * product.getHTPrice() * tax);
    }

    private Double computeProductHTPrice(Product product) {
        return product.getQuantity() * product.getHTPrice();
    }

    @Override
    public Map<Double, Double> computeTax(Product product) {
        Map<Double, Double> taxParamsMap = new HashMap<>(1);

        Double fullTaxes = product.getCategory().getTax();

        if (product.isImported())
            fullTaxes += IMPORT_TAXES;

        taxParamsMap.put(computeProductTTCPrice(product, fullTaxes), computeProductHTPrice(product));

        return taxParamsMap;
    }

}
