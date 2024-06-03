package com.example.lab02.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Setter
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name="products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private double price;
    private String description;
    @ElementCollection
    private List<String> image = new ArrayList<>();
//    private String image;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

}
