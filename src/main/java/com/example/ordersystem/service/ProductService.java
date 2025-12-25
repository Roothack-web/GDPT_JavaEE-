package com.example.ordersystem.service;

import com.example.ordersystem.entity.ApiResponse;
import com.example.ordersystem.entity.PageParam;
import com.example.ordersystem.entity.Product;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Service
@Validated
public class ProductService {
    
    @Autowired
    private com.example.ordersystem.mapper.ProductMapper productMapper;
    
    public ApiResponse<List<Product>> findAllProducts(PageParam pageParam, Map<String, Object> params) {
        PageHelper.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        List<Product> products = productMapper.findAll(params);
        PageInfo<Product> pageInfo = new PageInfo<>(products);
        
        return ApiResponse.success(
            products,
            pageInfo.getTotal(),
            pageParam.getPageNum(),
            pageParam.getPageSize()
        );
    }
    
    public ApiResponse<Product> findProductById(Long id) {
        Product product = productMapper.findById(id);
        if (product == null) {
            return ApiResponse.error("商品不存在");
        }
        return ApiResponse.success(product);
    }
    
    public ApiResponse<Product> findProductByCode(String productCode) {
        Product product = productMapper.findByProductCode(productCode);
        if (product == null) {
            return ApiResponse.error("商品不存在");
        }
        return ApiResponse.success(product);
    }
    
    @Transactional
    public ApiResponse<Product> addProduct(@Valid Product product) {
        Product existingProduct = productMapper.findByProductCode(product.getProductCode());
        if (existingProduct != null) {
            return ApiResponse.error("商品编码已存在");
        }
        
        if (product.getStatus() == null) {
            product.setStatus(1);
        }
        if (product.getStock() == null) {
            product.setStock(0);
        }
        
        int result = productMapper.insert(product);
        if (result > 0) {
            return ApiResponse.success(product);
        }
        return ApiResponse.error("添加商品失败");
    }
    
    @Transactional
    public ApiResponse<Product> updateProduct(@Valid Product product) {
        Product existingProduct = productMapper.findById(product.getId());
        if (existingProduct == null) {
            return ApiResponse.error("商品不存在");
        }
        
        if (!existingProduct.getProductCode().equals(product.getProductCode())) {
            Product duplicateProduct = productMapper.findByProductCode(product.getProductCode());
            if (duplicateProduct != null) {
                return ApiResponse.error("商品编码已存在");
            }
        }
        
        int result = productMapper.update(product);
        if (result > 0) {
            return ApiResponse.success(product);
        }
        return ApiResponse.error("更新商品失败");
    }
    
    @Transactional
    public ApiResponse<Void> deleteProduct(Long id) {
        Product product = productMapper.findById(id);
        if (product == null) {
            return ApiResponse.error("商品不存在");
        }
        
        int result = productMapper.deleteById(id);
        if (result > 0) {
            return ApiResponse.success(null);
        }
        return ApiResponse.error("删除商品失败");
    }
    
    @Transactional
    public ApiResponse<Void> batchDeleteProducts(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return ApiResponse.error("请选择要删除的商品");
        }
        
        int result = productMapper.batchDelete(ids);
        if (result > 0) {
            return ApiResponse.success(null);
        }
        return ApiResponse.error("批量删除商品失败");
    }
    
    @Transactional
    public ApiResponse<Void> updateStock(Long productId, Integer quantity) {
        int result = productMapper.decreaseStock(productId, quantity);
        if (result > 0) {
            return ApiResponse.success(null);
        }
        return ApiResponse.error("库存不足或商品不存在");
    }
}
