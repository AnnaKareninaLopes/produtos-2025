package edu.ifmg.produtos.util;

import edu.ifmg.produtos.dtos.ProductDTO;
import edu.ifmg.produtos.entities.Category;
import edu.ifmg.produtos.entities.Product;

public class Factory {

    public static Product createProduct() {

        Product product = new Product();

        product.setName("Iphone XXX");
        product.setPrice(5000.0);
        product.setImageUrl("http://img.com/iphonexxx.jpg");
        product.getCategories().add(new Category("News", 60L));

        return product;
    }

    public static ProductDTO createProductDTO() {
        Product product = createProduct();
        return new ProductDTO(product);
    }

}
