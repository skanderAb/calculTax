package com.adventium.taxcompute.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Double HTPrice;
    private int quantity;
    private boolean imported;

    @OneToOne(fetch = FetchType.EAGER)
    private ProductCategory category;

    @ManyToOne
    private Cart cart;
}
