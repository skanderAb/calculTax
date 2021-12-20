package com.adventium.taxcompute.services;

import com.adventium.taxcompute.model.Product;

import java.util.Map;

public interface ProductService {
    Map<Double, Double> computeTax(Product product);
}
