package com.adventium.taxcompute.services;

import com.adventium.taxcompute.model.Cart;

import java.util.List;

public interface CartService {
    Cart getCartById(Long cartId);

    List<Cart> getAllCarts();

    Double computeCartTax(Cart cart, StringBuilder str);
}
