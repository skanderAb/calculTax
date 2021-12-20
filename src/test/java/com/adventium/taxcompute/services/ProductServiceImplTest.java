package com.adventium.taxcompute.services;

import com.adventium.taxcompute.model.Product;
import com.adventium.taxcompute.model.ProductCategory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ProductServiceImplTest {

    public static final Long ID = 1L;
    public static final String NAME = "livres";

    ProductService productService;

    Product product;
    Product importedProduct;
    ProductCategory books;
    ProductCategory others;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        productService = new ProductServiceImpl();
    }

    @Test
    public void computeTax() {
        // given
        books = new ProductCategory();
        books.setId(ID);
        books.setCategory("Livre");
        books.setTax(1.1);

        product = new Product();
        product.setId(ID);
        product.setName(NAME);
        product.setHTPrice(12.49);
        product.setImported(false);
        product.setQuantity(2);
        product.setCategory(books);

        others = new ProductCategory();
        others.setId(2L);
        others.setCategory("Autre");
        others.setTax(1.2);

        importedProduct = new Product();
        importedProduct.setId(2L);
        importedProduct.setName("flacons de parfum importés");
        importedProduct.setHTPrice(47.50);
        importedProduct.setImported(true);
        importedProduct.setQuantity(3);
        importedProduct.setCategory(others);

        // when
        Map<Double, Double> taxParamsMap = productService.computeTax(product);
        Optional<Double> productTTCPrice = taxParamsMap.keySet().stream().findFirst();
        Optional<Double> productHTPrice = taxParamsMap.values().stream().findFirst();

        Map<Double, Double> importedTaxParamsMap = productService.computeTax(importedProduct);
        Optional<Double> importedProductTTCPrice = importedTaxParamsMap.keySet().stream().findFirst();

        // then
        assertEquals(Optional.of(27.5), productTTCPrice);
        assertNotEquals(Optional.of(27.5), productHTPrice);
        assertEquals(Optional.of(24.98), productHTPrice);
        assertEquals(1, taxParamsMap.size());

        assertEquals(Optional.of(178.15), importedProductTTCPrice);
    }
}
