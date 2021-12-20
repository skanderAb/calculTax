package com.adventium.taxcompute.services;

import com.adventium.taxcompute.model.Cart;
import com.adventium.taxcompute.repositories.CartRepository;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class CartServiceImpl implements CartService {

    private final ProductService productService;
    private final CartRepository cartRepository;

    public CartServiceImpl(ProductService productService, CartRepository cartRepository) {
        this.productService = productService;
        this.cartRepository = cartRepository;
    }

    @Override
    public Cart getCartById(Long cartId) {
        return cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart Not Found for ID value: " + cartId));
    }

    @Override
    public List<Cart> getAllCarts() {
        return cartRepository.findAll();
    }

    public Double computeCartTax(Cart cart, StringBuilder str) {
        // Convert the Double to Atomic reference because we cannot use a non final variable inside a lambda expression
        AtomicReference<Double> cartTTCPrice = new AtomicReference<>(0.0);
        AtomicReference<Double> cartHTPrice = new AtomicReference<>(0.0);

        str.append("#### ").append(cart.getName()).append("\n\n");

        // Calculate The TTC Price for all the shopping cart
        cart.getProducts().forEach(product -> {

            // Compute the Tax for each product inside the shopping cart
            Map<Double, Double> taxParamsMap = productService.computeTax(product);
            Double productTTCPrice = taxParamsMap.keySet().stream().findFirst().orElse(0.0);
            Double productHTPrice = taxParamsMap.values().stream().findFirst().orElse(0.0);

            // Append each product quantity, unit price and ttc price
            str.append("* ").append(product.getQuantity()).append(" ").append(product.getName()).append(" à ").append(product.getHTPrice()).append("€ : ").append(productTTCPrice).append("€ TTC\n");

            // Update the TTC Price of the shopping cart
            cartTTCPrice.updateAndGet(v -> v + productTTCPrice);

            // Update the HT Price of the shopping cart
            cartHTPrice.updateAndGet(v -> v + productHTPrice);
        });

        // Append the total amount of taxes
        str.append("\nMontant des taxes : ").append(computeAndFormatTotalTaxes(cartTTCPrice.get(), cartHTPrice.get())).append("€");

        // Append the total cart TTC price
        str.append("\nTotal : ").append(cartTTCPrice).append("€\n\n");


        return cartTTCPrice.get();
    }

    private String computeAndFormatTotalTaxes(Double ttcPrice, Double htPrice) {
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.getDefault());
        otherSymbols.setDecimalSeparator('.');
        otherSymbols.setGroupingSeparator(' ');
        DecimalFormat df = new DecimalFormat("0.00", otherSymbols);

        return df.format(ttcPrice - htPrice);
    }
}
