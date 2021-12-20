package com.adventium.taxcompute.services;

import com.adventium.taxcompute.model.Cart;
import com.adventium.taxcompute.model.Product;
import com.adventium.taxcompute.model.ProductCategory;
import com.adventium.taxcompute.repositories.CartRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

public class CartServiceImplTest {

    public static final Long ID = 1L;
    public static final String NAME = "New shopping cart";

    CartService cartService;

    @Mock
    ProductService productService;

    @Mock
    CartRepository cartRepository;

    Cart shoppingCart;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        cartService = new CartServiceImpl(productService, cartRepository);

        shoppingCart = new Cart();
        shoppingCart.setId(1L);
        shoppingCart.setName("New shopping cart");
    }

    @Test
    public void getCartById() {
        //given
        given(cartRepository.findById(anyLong())).willReturn(Optional.of(shoppingCart));

        //when
        Cart cart = cartService.getCartById(ID);

        //then
        assertEquals(NAME, cart.getName());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void getCartByIdNotFound() {
        given(cartRepository.findById(anyLong())).willReturn(Optional.empty());

        cartService.getCartById(ID);

        then(cartRepository).should(times(1)).findById(anyLong());
    }

    @Test
    public void computeCartTax() {
        // given
        ProductCategory firstNecessity = new ProductCategory();
        firstNecessity.setId(ID);
        firstNecessity.setCategory("Première nécessité");
        firstNecessity.setTax(1.0);

        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("boîtes de chocolats importées");
        product1.setHTPrice(10.00);
        product1.setImported(true);
        product1.setQuantity(2);
        product1.setCategory(firstNecessity);
        product1.setCart(shoppingCart);

        shoppingCart.getProducts().add(product1);

        Map<Double, Double> taxParamsMap = new HashMap<>(1);
        taxParamsMap.put(21.0, product1.getHTPrice() * product1.getQuantity());

        given(productService.computeTax(any(Product.class))).willReturn(taxParamsMap);

        // when
        Double cartTTCPrice = cartService.computeCartTax(shoppingCart, new StringBuilder());

        // then
        then(productService).should(times(1)).computeTax(any(Product.class));
        assertEquals(21.0, cartTTCPrice, 0.01);
    }
}
