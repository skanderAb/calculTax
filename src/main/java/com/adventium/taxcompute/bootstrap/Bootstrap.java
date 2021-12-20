package com.adventium.taxcompute.bootstrap;

import com.adventium.taxcompute.dto.Bill;
import com.adventium.taxcompute.model.Cart;
import com.adventium.taxcompute.model.Product;
import com.adventium.taxcompute.model.ProductCategory;
import com.adventium.taxcompute.repositories.CartRepository;
import com.adventium.taxcompute.repositories.ProductCategoryRepository;
import com.adventium.taxcompute.repositories.ProductRepository;
import com.adventium.taxcompute.services.CartService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

@Component
public class Bootstrap implements CommandLineRunner {

    private final CartService cartService;

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;

    private ProductCategory firstNecessity;
    private ProductCategory books;
    private ProductCategory others;

    public Bootstrap(CartService cartService, CartRepository cartRepository, ProductRepository productRepository,
                     ProductCategoryRepository productCategoryRepository) {
        this.cartService = cartService;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.productCategoryRepository = productCategoryRepository;
    }

    @Override
    public void run(String... args) {
        LoadProductCategories();
        LoadProducts();
        PrintBill();
    }

    private void LoadProductCategories() {
        firstNecessity = productCategoryRepository.getById(1L);
        books = productCategoryRepository.getById(2L);
        others = productCategoryRepository.getById(3L);
    }

    private void LoadProducts() {
        List<Product> inputs = new ArrayList<>();

        //#### Input 1
        Cart cart1 = new Cart();
        cart1.setName("Output1");

        Product product1 = new Product();
        product1.setName("Livre");
        product1.setHTPrice(12.49);
        product1.setQuantity(2);
        product1.setImported(false);
        product1.setCategory(books);
        product1.setCart(cart1);

        Product product2 = new Product();
        product2.setName("CD musical");
        product2.setHTPrice(14.99);
        product2.setQuantity(1);
        product2.setImported(false);
        product2.setCategory(others);
        product2.setCart(cart1);

        Product product3 = new Product();
        product3.setName("Barres de chocolat");
        product3.setHTPrice(0.85);
        product3.setQuantity(3);
        product3.setImported(false);
        product3.setCategory(firstNecessity);
        product3.setCart(cart1);

        inputs.add(product1);
        inputs.add(product2);
        inputs.add(product3);
        cartRepository.save(cart1);

        //#### Input 2
        Cart cart2 = new Cart();
        cart2.setName("Output2");

        Product product4 = new Product();
        product4.setName("Boîtes de chocolats importées");
        product4.setHTPrice(10.00);
        product4.setQuantity(2);
        product4.setImported(true);
        product4.setCategory(firstNecessity);
        product4.setCart(cart2);

        Product product5 = new Product();
        product5.setName("Flacons de parfum importés");
        product5.setHTPrice(47.50);
        product5.setQuantity(3);
        product5.setImported(true);
        product5.setCategory(others);
        product5.setCart(cart2);

        inputs.add(product4);
        inputs.add(product5);
        cartRepository.save(cart2);

        //#### Input 3
        Cart cart3 = new Cart();
        cart3.setName("Output3");

        Product product6 = new Product();
        product6.setName("Flacons de parfum importés");
        product6.setHTPrice(27.99);
        product6.setQuantity(2);
        product6.setImported(true);
        product6.setCategory(others);
        product6.setCart(cart3);

        Product product7 = new Product();
        product7.setName("Flacon de parfum");
        product7.setHTPrice(18.99);
        product7.setQuantity(1);
        product7.setImported(false);
        product7.setCategory(others);
        product7.setCart(cart3);

        Product product8 = new Product();
        product8.setName("Boîtes de pilules contre la migraine");
        product8.setHTPrice(9.75);
        product8.setQuantity(3);
        product8.setImported(false);
        product8.setCategory(firstNecessity);
        product8.setCart(cart3);

        Product product9 = new Product();
        product9.setName("Boîtes de chocolats importées");
        product9.setHTPrice(11.25);
        product9.setQuantity(2);
        product9.setImported(true);
        product9.setCategory(firstNecessity);
        product9.setCart(cart3);

        inputs.add(product6);
        inputs.add(product7);
        inputs.add(product8);
        inputs.add(product9);
        cartRepository.save(cart3);

        productRepository.saveAll(inputs);

        System.out.println("Products Loaded: " + productRepository.count());
    }

    private void PrintBill() {
        // The path where the bill will be created
        Path file = Paths.get(System.getProperty("user.home") + "/Documents/Facture_0001.txt");

        // Create the set of options for appending to the file.
        Set<OpenOption> options = new HashSet<>();
        options.add(WRITE);
        options.add(TRUNCATE_EXISTING);

        StringBuilder str = new StringBuilder("### OUTPUT\n\n");

        // Create a new Bill by getting carts from DB
        Bill bill = new Bill(cartService.getAllCarts());

        // Compute the cart's TTC Price and the total tax amount
        bill.getCarts().forEach(cart -> cartService.computeCartTax(cart, str));

        // Convert the string to a ByteBuffer.
        byte[] data = str.toString().getBytes();
        ByteBuffer bb = ByteBuffer.wrap(data);

        try (SeekableByteChannel sbc =
                     Files.newByteChannel(file, options)) {

            // Write into the bill file
            sbc.write(bb);
        } catch (IOException x) {
            System.out.println("Exception thrown: " + x);
        }
    }
}
