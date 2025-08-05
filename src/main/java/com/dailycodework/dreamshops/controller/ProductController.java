package com.dailycodework.dreamshops.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dailycodework.dreamshops.dto.ProductDto;
import com.dailycodework.dreamshops.model.Product;
import com.dailycodework.dreamshops.request.AddProductRequest;
import com.dailycodework.dreamshops.request.ProductUpdateRequest;
import com.dailycodework.dreamshops.response.ApiResponse;
import com.dailycodework.dreamshops.service.product.IProductService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    private final IProductService productService;

    @GetMapping
    public ResponseEntity<ApiResponse> getProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String name) {
        try {
            List<Product> products;

            // Apply filters based on provided parameters
            if (category != null && brand != null && name != null) {
                products = productService
                        .getProductsByCategoryAndBrand(category, brand).stream()
                        .filter(product -> product.getName().toLowerCase()
                                .contains(name.toLowerCase()))
                        .toList();
            } else if (category != null && brand != null) {
                products = productService
                        .getProductsByCategoryAndBrand(category, brand);
            } else if (brand != null && name != null) {
                products = productService.getProductsByBrandAndName(brand,
                        name);
            } else if (category != null) {
                products = productService.getProductsbyCategory(category);
            } else if (brand != null) {
                products = productService.getProductsByBrand(brand);
            } else if (name != null) {
                // name should be at least 3 characters for partial match
                if (name.length() > 2) {
                    products = productService
                            .getProductsByNameContainingIgnoreCase(name);
                } else {
                    // return empty list if name is too short
                    products = List.of();
                }
            } else {
                // If no parameters provided, return all products
                products = productService.getAllProducts();
            }

            if (products.isEmpty()) {
                return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(
                        "No products found matching the criteria!", null));
            }

            // Convert to DTOs
            List<ProductDto> productDtos = productService
                    .toProductDtoList(products);
            return ResponseEntity.ok(new ApiResponse("Success!", productDtos));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse> getProductById(
            @PathVariable Long productId) {
        try {
            Product product = productService.getProductById(productId);
            ProductDto productDto = productService.toProductDto(product);
            return ResponseEntity.ok(new ApiResponse("success", productDto));
        } catch (Exception e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addProduct(
            @RequestBody AddProductRequest product) {
        try {
            Product addedProduct = productService.addProduct(product);
            ProductDto productDto = productService.toProductDto(addedProduct);
            return ResponseEntity.ok(
                    new ApiResponse("Product added successfully", productDto));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(e.getMessage(), null));
        }

    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{productId}/update")
    public ResponseEntity<ApiResponse> updateProduct(
            @RequestBody ProductUpdateRequest product,
            @PathVariable Long productId) {
        try {
            Product theProduct = productService.updateProduct(product,
                    productId);
            ProductDto productDto = productService.toProductDto(theProduct);
            return ResponseEntity.ok(new ApiResponse(
                    "Product updated successfully", productDto));
        } catch (Exception e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{productId}/delete")
    public ResponseEntity<ApiResponse> deleteProductById(
            @PathVariable Long productId) {
        try {
            productService.deleteProductById(productId);
            return ResponseEntity
                    .ok(new ApiResponse("Product deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/count")
    public ResponseEntity<ApiResponse> countProductsByBrandAndName(
            @RequestParam String brand, @RequestParam String name) {
        try {
            var productCount = productService.countProductsByBrandAndName(brand,
                    name);
            return ResponseEntity
                    .ok(new ApiResponse("Product count!", productCount));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }
}
