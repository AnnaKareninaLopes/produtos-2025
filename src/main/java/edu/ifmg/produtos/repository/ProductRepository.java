package edu.ifmg.produtos.repository;

import edu.ifmg.produtos.entities.Product;
import edu.ifmg.produtos.projections.ProductProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query(nativeQuery = true,
            value = """
                select * from (
                    SELECT DISTINCT p.id, p.name, p.image_url, p.price
                    FROM tb_product p
                    INNER JOIN tb_product_category pc ON pc.product_id = p.id
                    WHERE (pc.category_id in :categoriesID)
                        and LOWER(p.name) like LOWER( CONCAT('%',:name,'%') )
                ) as tb_result
            """,
            countQuery = """
                select count(*) from (
                    SELECT DISTINCT p.id, p.name, p.image_url, p.price
                    FROM tb_product p
                    INNER JOIN tb_product_category pc ON pc.product_id = p.id
                    WHERE (pc.category_id in :categoriesID) 
                        and LOWER(p.name) like LOWER( CONCAT('%',:name,'%') )
                ) as tb_result 
            """
    )
    public Page<ProductProjection> searchProductsWithCategories(List<Long> categoriesID, String name, Pageable pageable);

    @Query(nativeQuery = true,
            value = """ 
                select * from (
                    SELECT DISTINCT p.id, p.name, p.image_url, p.price 
                    FROM tb_product p
                    INNER JOIN tb_product_category pc ON pc.product_id = p.id 
                    WHERE LOWER(p.name) like LOWER( CONCAT('%',:name,'%') )
                ) as tb_result
            """,
            countQuery = """ 
                select count(*) from (
                    SELECT DISTINCT p.id, p.name, p.image_url, p.price 
                    FROM tb_product p 
                    INNER JOIN tb_product_category pc ON pc.product_id = p.id 
                    WHERE LOWER(p.name) like LOWER( CONCAT('%',:name,'%') )
                ) as tb_result
            """)
    public Page<ProductProjection> searchProductsWithoutCategories(String name, Pageable pageable);
}