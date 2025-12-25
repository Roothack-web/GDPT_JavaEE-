package com.example.ordersystem.mapper;

import com.example.ordersystem.entity.Product;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface ProductMapper {
    
    List<Product> findAll(Map<String, Object> params);
    
    @Select("SELECT * FROM product WHERE id = #{id} AND is_deleted = 0")
    Product findById(Long id);
    
    @Select("SELECT * FROM product WHERE product_code = #{productCode} AND is_deleted = 0")
    Product findByProductCode(String productCode);
    
    @Insert("INSERT INTO product(product_name, product_code, description, price, stock, status, " +
            "category, image_url, create_by, update_by) " +
            "VALUES(#{productName}, #{productCode}, #{description}, #{price}, #{stock}, #{status}, " +
            "#{category}, #{imageUrl}, #{createBy}, #{updateBy})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Product product);
    
    @Update("UPDATE product SET product_name = #{productName}, product_code = #{productCode}, " +
            "description = #{description}, price = #{price}, stock = #{stock}, status = #{status}, " +
            "category = #{category}, image_url = #{imageUrl}, update_by = #{updateBy} " +
            "WHERE id = #{id} AND is_deleted = 0")
    int update(Product product);
    
    @Update("UPDATE product SET is_deleted = 1 WHERE id = #{id} AND is_deleted = 0")
    int deleteById(Long id);
    
    @Update("<script>" +
            "UPDATE product SET is_deleted = 1 WHERE id IN " +
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach> AND is_deleted = 0" +
            "</script>")
    int batchDelete(@Param("ids") List<Long> ids);
    
    @Update("UPDATE product SET stock = stock - #{quantity} WHERE id = #{productId} " +
            "AND stock >= #{quantity} AND is_deleted = 0 AND status = 1")
    int decreaseStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);
    
    @Select("SELECT COUNT(*) FROM product WHERE is_deleted = 0")
    int count();
    
    @Select("SELECT COUNT(*) FROM product WHERE id = #{id} AND is_deleted = 0 AND status = 1")
    int checkExists(Long id);
}
