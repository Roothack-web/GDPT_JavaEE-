package com.example.ordersystem.controller;

import com.example.ordersystem.entity.ApiResponse;
import com.example.ordersystem.entity.PageParam;
import com.example.ordersystem.entity.Product;
import com.example.ordersystem.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@Validated
public class ProductController {

    @Autowired
    private ProductService productService;

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    // 查询所有商品（分页）
    @GetMapping
    public ApiResponse<?> findAllProducts(
            @Valid PageParam pageParam,
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) String productCode,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice) {

        // 使用 HashMap 替代 Map.of()
        Map<String, Object> params = new HashMap<>();
        if (productName != null) params.put("productName", productName);
        if (productCode != null) params.put("productCode", productCode);
        if (category != null) params.put("category", category);
        if (status != null) params.put("status", status);
        if (minPrice != null) params.put("minPrice", minPrice);
        if (maxPrice != null) params.put("maxPrice", maxPrice);

        return productService.findAllProducts(pageParam, params);
    }

    @GetMapping("/{id}")
    public ApiResponse<Product> findProductById(@PathVariable Long id) {
        return productService.findProductById(id);
    }

    @GetMapping("/code/{productCode}")
    public ApiResponse<Product> findProductByCode(@PathVariable String productCode) {
        return productService.findProductByCode(productCode);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Product> addProduct(@Valid @RequestBody Product product) {
        return productService.addProduct(product);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Product> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody Product product) {
        product.setId(id);
        return productService.updateProduct(product);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteProduct(@PathVariable Long id) {
        return productService.deleteProduct(id);
    }

    @DeleteMapping("/batch")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> batchDeleteProducts(@RequestBody @NotEmpty List<Long> ids) {
        return productService.batchDeleteProducts(ids);
    }

    @PostMapping("/upload-image")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> uploadProductImage(@RequestParam("file") MultipartFile file,
                                                  @RequestParam("productId") Long productId) {
        try {
            if (file.isEmpty()) {
                return ApiResponse.error("请选择文件");
            }

            // 创建上传目录
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFilename = UUID.randomUUID().toString() + fileExtension;

            // 保存文件
            Path path = Paths.get(uploadDir, newFilename);
            Files.copy(file.getInputStream(), path);

            // 更新商品图片URL - 确保是正确的URL路径
            String imageUrl = "/uploads/" + newFilename;  // 使用相对路径

            Product product = productService.findProductById(productId).getData();
            if (product != null) {
                product.setImageUrl(imageUrl);
                productService.updateProduct(product);
                return ApiResponse.success(imageUrl);
            }

            return ApiResponse.error("商品不存在");
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("上传失败: " + e.getMessage());
        }
    }}