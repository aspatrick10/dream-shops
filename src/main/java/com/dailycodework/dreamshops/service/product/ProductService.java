package com.dailycodework.dreamshops.service.product;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.dailycodework.dreamshops.dto.ProductDto;
import com.dailycodework.dreamshops.exceptions.ResourceNotFoundException;
import com.dailycodework.dreamshops.model.Category;
import com.dailycodework.dreamshops.model.Product;
import com.dailycodework.dreamshops.repository.CategoryRepository;
import com.dailycodework.dreamshops.repository.ProductRepository;
import com.dailycodework.dreamshops.request.AddProductRequest;
import com.dailycodework.dreamshops.request.ProductUpdateRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    @Override
    public Product addProduct(AddProductRequest product) {
        // check if category exists in the db
        // if yes, set it as the category of the product
        // if no, create a new category and set it as the category of the
        // product
        Category category = categoryRepository
                .findByName(product.getCategory().getName());

        if (category == null) {
            category = new Category();
            category.setName(product.getCategory().getName());
            category = categoryRepository.save(category);
        }

        return productRepository.save(new Product(product.getName(),
                product.getBrand(), product.getPrice(), product.getInventory(),
                product.getDescription(), category));

    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Product not found"));
    }

    @Override
    public void deleteProductById(Long id) {
        productRepository.findById(id)
                .ifPresentOrElse(productRepository::delete, () -> {
                    throw new ResourceNotFoundException("Product not found!");
                });
    }

    @Override
    public Product updateProduct(ProductUpdateRequest product, Long productId) {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found for update"));

        existingProduct.setName(product.getName());
        existingProduct.setBrand(product.getBrand());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setInventory(product.getInventory());
        existingProduct.setDescription(product.getDescription());

        Category category = categoryRepository
                .findByName(product.getCategory().getName());

        if (category == null) {
            category = new Category();
            category.setName(product.getCategory().getName());
            category = categoryRepository.save(category);
        }

        existingProduct.setCategory(category);
        return productRepository.save(existingProduct);
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public List<Product> getProductsbyCategory(String category) {
        return productRepository.findByCategoryName(category);
    }

    @Override
    public List<Product> getProductsByBrand(String brand) {
        return productRepository.findByBrand(brand);
    }

    @Override
    public List<Product> getProductsByCategoryAndBrand(String category,
            String brand) {
        return productRepository.findByCategoryNameAndBrand(category, brand);
    }

    @Override
    public List<Product> getProductsByName(String name) {
        return productRepository.findByName(name);
    }

    @Override
    public List<Product> getProductsByBrandAndName(String brand, String name) {
        return productRepository.findByBrandAndName(brand, name);
    }

    @Override
    public Long countProductsByBrandAndName(String brand, String name) {
        return productRepository.countByBrandAndName(brand, name);
    }

    // Utility DTO conversion methods
    @Override
    public ProductDto toProductDto(Product product) {
        return modelMapper.map(product, ProductDto.class);
    }

    @Override
    public List<ProductDto> toProductDtoList(List<Product> products) {
        return products.stream().map(this::toProductDto).toList();
    }

    // Partial match method (case-insensitive)
    @Override
    public List<Product> getProductsByNameContainingIgnoreCase(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }
}
