package edu.ifmg.produtos.entities;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="tb_product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private Double price;
    private String imageUrl;

    private Instant createdAt;
    private Instant updatedAt;

    @ManyToMany
    @JoinTable( // Tabela intermediária
            name = "tb_product_category",
            joinColumns = @JoinColumn(name = "product_id"), // Chave estrangeira para a tabela de produtos
            inverseJoinColumns = @JoinColumn(name = "category_id") // Chave estrangeira para a tabela de categorias
    )
    private Set<Category> categories = new HashSet<>();

    public Product() {

    }

    public Product(String name, String description, Double price, String imageUrl) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
    }
    /*
    public Product(Product entity) {
        this.id = entity.getId();
    }

    public Product(Product product, Set<Category> categories) {
        this(product);
    }
    */
}
