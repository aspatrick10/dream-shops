package com.dailycodework.dreamshops.service.product;

import java.util.List;

import com.dailycodework.dreamshops.dto.ProductDto;
import com.dailycodework.dreamshops.model.Product;
import com.dailycodework.dreamshops.request.AddProductRequest;
import com.dailycodework.dreamshops.request.ProductUpdateRequest;

public interface IProductService {
    Product addProduct(AddProductRequest product);

    Product getProductById(Long id);

    void deleteProductById(Long id);

    Product updateProduct(ProductUpdateRequest product, Long productId);

    List<Product> getAllProducts();

    // Exact match methods
    List<Product> getProductsbyCategory(String category);

    List<Product> getProductsByBrand(String brand);

    List<Product> getProductsByCategoryAndBrand(String category, String brand);

    List<Product> getProductsByName(String name);

    List<Product> getProductsByBrandAndName(String brand, String name);

    Long countProductsByBrandAndName(String brand, String name);

    // Utility DTO conversion methods
    ProductDto toProductDto(Product product);

    List<ProductDto> toProductDtoList(List<Product> products);

    // Partial match methods (case-insensitive)
    List<Product> getProductsByNameContainingIgnoreCase(String name);
}
