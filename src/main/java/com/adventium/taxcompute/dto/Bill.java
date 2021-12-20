package com.adventium.taxcompute.dto;

import com.adventium.taxcompute.model.Cart;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class Bill {
    List<Cart> carts;
}
